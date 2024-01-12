/*
 * Copyright (c) 2024 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.service.impl;

import jakarta.ejb.Singleton;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.modelmapper.TypeToken;
import pl.polsl.skirentalservice.core.ModelMapperBean;
import pl.polsl.skirentalservice.core.db.PersistenceBean;
import pl.polsl.skirentalservice.core.mail.Attachment;
import pl.polsl.skirentalservice.core.mail.MailRequestPayload;
import pl.polsl.skirentalservice.core.mail.MailServiceBean;
import pl.polsl.skirentalservice.core.mail.MailTemplate;
import pl.polsl.skirentalservice.core.s3.S3Bucket;
import pl.polsl.skirentalservice.core.s3.S3ClientBean;
import pl.polsl.skirentalservice.core.servlet.WebServletRequest;
import pl.polsl.skirentalservice.core.servlet.pageable.ServletPagination;
import pl.polsl.skirentalservice.core.servlet.pageable.Slice;
import pl.polsl.skirentalservice.dao.*;
import pl.polsl.skirentalservice.dao.hibernate.*;
import pl.polsl.skirentalservice.dto.*;
import pl.polsl.skirentalservice.dto.deliv_return.*;
import pl.polsl.skirentalservice.dto.login.LoggedUserDataDto;
import pl.polsl.skirentalservice.dto.rent.RentEquipmentsDetailsResDto;
import pl.polsl.skirentalservice.entity.*;
import pl.polsl.skirentalservice.exception.AlreadyExistException;
import pl.polsl.skirentalservice.exception.DateException;
import pl.polsl.skirentalservice.exception.NotFoundException;
import pl.polsl.skirentalservice.exception.RentException;
import pl.polsl.skirentalservice.pdf.ReturnPdfDocumentGenerator;
import pl.polsl.skirentalservice.pdf.dto.GeneratedPdfData;
import pl.polsl.skirentalservice.pdf.dto.PdfDocumentData;
import pl.polsl.skirentalservice.pdf.dto.PdfEquipmentDataDto;
import pl.polsl.skirentalservice.service.ReturnService;
import pl.polsl.skirentalservice.util.CurrencyUtils;
import pl.polsl.skirentalservice.util.DateUtils;
import pl.polsl.skirentalservice.util.RentStatus;
import pl.polsl.skirentalservice.util.UserRole;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Singleton
@SuppressWarnings("unused")
public class ReturnServiceBean implements ReturnService {
    private final PersistenceBean persistenceBean;
    private final S3ClientBean s3ClientBean;
    private final ModelMapperBean modelMapperBean;
    private final MailServiceBean mailServiceBean;

    @Inject
    public ReturnServiceBean(
        PersistenceBean persistenceBean,
        S3ClientBean s3ClientBean,
        ModelMapperBean modelMapperBean,
        MailServiceBean mailServiceBean
    ) {
        this.persistenceBean = persistenceBean;
        this.s3ClientBean = s3ClientBean;
        this.modelMapperBean = modelMapperBean;
        this.mailServiceBean = mailServiceBean;
    }

    @Override
    public Slice<OwnerRentReturnRecordResDto> getPageableOwnerReturns(PageableDto pageableDto) {
        return persistenceBean.startNonTransactQuery(session -> {
            final ReturnDao returnDao = new ReturnDaoHib(session);

            final Long totalReturns = returnDao.findAllReturnsCount(pageableDto.filterData());
            final ServletPagination pagination = new ServletPagination(pageableDto.page(),
                pageableDto.total(), totalReturns);
            if (pagination.checkIfIsInvalid()) {
                return new Slice<>(pagination);
            }
            final List<OwnerRentReturnRecordResDto> returnsList = returnDao.findAllPageableReturnsRecords(pageableDto);
            return new Slice<>(pagination, returnsList);
        });
    }

    @Override
    public Slice<SellerRentReturnRecordResDto> getPageableEmployerReturns(PageableDto pageableDto, Long employerId) {
        return persistenceBean.startNonTransactQuery(session -> {
            final ReturnDao returnDao = new ReturnDaoHib(session);
            final Long totalReturns = returnDao.findAllReturnsFromEmployerCount(pageableDto.filterData(), employerId);

            final ServletPagination pagination = new ServletPagination(pageableDto.page(),
                pageableDto.total(), totalReturns);
            if (pagination.checkIfIsInvalid()) {
                return new Slice<>(pagination);
            }
            final List<SellerRentReturnRecordResDto> returnsList = returnDao
                .findAllPageableReturnsFromEmployerRecords(pageableDto, employerId);

            return new Slice<>(pagination, returnsList);
        });
    }

    @Override
    public MultipleEquipmentsDataDto<ReturnRentDetailsResDto> getReturnDetails(
        Long returnId, LoggedUserDataDto loggedUser
    ) {
        return persistenceBean.startNonTransactQuery(session -> {
            final ReturnDao returnDao = new ReturnDaoHib(session);
            final EquipmentDao equipmentDao = new EquipmentDaoHib(session);

            final var returnDetails = returnDao
                .findReturnDetails(returnId, loggedUser.getId(), String.valueOf(loggedUser.getRoleAlias()))
                .orElseThrow(NotFoundException.ReturnNotFoundException::new);

            final List<RentEquipmentsDetailsResDto> allReturnEquipments = equipmentDao
                .findAllEquipmentsConnectedWithReturn(returnId);

            final Integer totalSum = allReturnEquipments.stream()
                .map(RentEquipmentsDetailsResDto::count).reduce(0, Integer::sum);

            return new MultipleEquipmentsDataDto<>(returnDetails, allReturnEquipments, totalSum);
        });
    }

    @Override
    public GeneratedReturnData generateReturn(
        Long rentId, String description, LoggedUserDataDto loggedUser, WebServletRequest req
    ) {
        final AtomicReference<String> generatedFileKey = new AtomicReference<>(StringUtils.EMPTY);
        return persistenceBean.startTransaction(session -> {
            final EquipmentDao equipmentDao = new EquipmentDaoHib(session);
            final CustomerDao customerDao = new CustomerDaoHib(session);
            final RentDao rentDao = new RentDaoHib(session);
            final ReturnDao returnDao = new ReturnDaoHib(session);

            if (!customerDao.checkIfCustomerByRentIdExist(rentId)) {
                throw new RentException.RentHasDeletedCustomerException();
            }
            returnDao
                .findReturnExistDocument(rentId)
                .ifPresent(returnDocument -> {
                    throw new AlreadyExistException.ReturnDocumentAlreadyExistException(req,
                        returnDocument.returnIdentifier(), returnDocument.id());
                });
            final RentReturnDetailsResDto rentDetails = rentDao
                .findRentReturnDetails(rentId, loggedUser.getId())
                .orElseThrow(NotFoundException.RentNotFoundException::new);

            final List<RentReturnEquipmentRecordResDto> equipmentsList = equipmentDao
                .findAllEquipmentsConnectedWithRentReturn(rentId);
            if (equipmentsList.isEmpty()) {
                throw new NotFoundException.EquipmentNotFoundException();
            }
            final LocalDateTime generatedBrief = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
            final String returnIssuerIdentifier = rentDetails.issuedIdentifier().replace("WY", "ZW");
            if (rentDetails.rentDateTime().isAfter(generatedBrief)) {
                throw new DateException.ReturnDateBeforeRentDateException();
            }
            // get start and end renting time total hours (scale up)
            final BriefTimeData briefTimeData = DateUtils
                .getHoursAndDaysFromBriefTime(rentDetails.rentDateTime(), generatedBrief);

            final RentEntity rentEntity = session.get(RentEntity.class, rentId);
            final RentReturnEntity rentReturn = modelMapperBean.map(rentDetails, RentReturnEntity.class);
            rentReturn.setTotalDepositPrice(rentDetails.totalDepositPriceNetto());
            rentReturn.setEquipments(new HashSet<>());

            final Set<EmailEquipmentPayloadDataDto> emailEquipmentsPayload = new HashSet<>();
            final BigDecimal totalSumPriceNetto = recalculatePricesAndPersistEquipments(
                rentReturn, equipmentsList, briefTimeData, rentDetails.tax(), session, equipmentDao,
                emailEquipmentsPayload);

            rentReturn.setIssuedIdentifier(returnIssuerIdentifier);
            rentReturn.setIssuedDateTime(generatedBrief);
            rentReturn.setDescription(description);
            rentReturn.setRent(rentEntity);

            rentDao.updateRentStatus(RentStatus.RETURNED, rentId);
            session.persist(rentReturn);

            final CustomerDetailsReturnResDto customerDetails = customerDao
                .findCustomerDetailsForReturnDocument(rentId)
                .orElseThrow(() -> new NotFoundException.UserNotFoundException(UserRole.USER));

            final var emailPayload = generateEmailPayload(rentDetails, customerDetails,
                totalSumPriceNetto, generatedBrief, briefTimeData, emailEquipmentsPayload);

            final Attachment pdfReturnDocument = generatePdfReturnDocument(rentDetails, customerDetails,
                generatedBrief, briefTimeData, description, returnIssuerIdentifier, emailPayload);
            s3ClientBean.putObject(S3Bucket.RETURNS, pdfReturnDocument);
            generatedFileKey.set(pdfReturnDocument.name());

            sendReturnEmailMessages(session, emailPayload, description, rentReturn, rentDetails,
                req, pdfReturnDocument, loggedUser);

            session.getTransaction().commit();

            log.info("Successfully generated return from rent with id: {} with id: {} by user: {}",
                rentDetails.issuedIdentifier(), returnIssuerIdentifier, loggedUser.getLogin());
            return new GeneratedReturnData(rentDetails.issuedIdentifier(), returnIssuerIdentifier);
        }, () -> {
            final String generatedFileKeyValue = generatedFileKey.get();
            if (!generatedFileKeyValue.equals(StringUtils.EMPTY)) {
                s3ClientBean.deleteObject(S3Bucket.RETURNS, generatedFileKeyValue);
            }
        });
    }

    @Override
    public String deleteReturn(Long returnId, LoggedUserDataDto loggedUser) {
        return persistenceBean.startTransaction(session -> {
            final EquipmentDao equipmentDao = new EquipmentDaoHib(session);
            final CustomerDao customerDao = new CustomerDaoHib(session);
            final RentDao rentDao = new RentDaoHib(session);
            final ReturnDao returnDao = new ReturnDaoHib(session);

            final RentReturnEntity rentReturn = session.getReference(RentReturnEntity.class, returnId);
            if (rentReturn == null) {
                throw new NotFoundException.ReturnNotFoundException();
            }
            final RentEntity rent = rentReturn.getRent();
            final Long rentId = rentReturn.getRent().getId();
            rentDao.updateRentStatus(RentStatus.RENTED, rentId);

            for (final RentEquipmentEntity equipment : rent.getEquipments()) {
                final EquipmentEntity eq = equipment.getEquipment();
                if (eq != null && rent.getCustomer() != null) {
                    if ((eq.getAvailableCount() - equipment.getCount()) < 0) {
                        throw new RentException.TooLowAvailableEquipmentsForDeleteReturnException();
                    }
                    equipmentDao.decreaseAvailableSelectedEquipmentCount(equipment.getEquipment().getId(),
                        equipment.getCount());
                }
            }
            final String identifier = rentReturn.getIssuedIdentifier();
            session.remove(rentReturn);

            final String fileName = rentReturn.getIssuedIdentifier().replaceAll("/", "-") + ".pdf";
            s3ClientBean.deleteObject(S3Bucket.RETURNS, fileName);

            session.getTransaction().commit();

            log.info("Rent return with id: {} was succesfuly removed from system by {}. Rent data: {}", returnId,
                loggedUser.getLogin(), rentReturn);
            return identifier;
        });
    }

    @Override
    public boolean checkIfReturnExist(Long returnId) {
        return persistenceBean.startNonTransactQuery(session -> {
            final ReturnDao returnDao = new ReturnDaoHib(session);
            return returnDao.checkIfReturnExist(returnId);
        });
    }

    @Override
    public boolean checkIfReturnIsFromEmployer(Long returnId, Long employerId) {
        return persistenceBean.startNonTransactQuery(session -> {
            final ReturnDao returnDao = new ReturnDaoHib(session);
            return returnDao.checkIfReturnIsFromEmployer(returnId, employerId);
        });
    }

    private BigDecimal recalculatePricesAndPersistEquipments(
        RentReturnEntity rentReturn,
        List<RentReturnEquipmentRecordResDto> equipmentsList,
        BriefTimeData briefTimeData,
        int taxValue,
        Session session,
        EquipmentDao equipmentDao,
        Set<EmailEquipmentPayloadDataDto> emailEquipmentsPayload
    ) {
        final Set<RentReturnEquipmentEntity> rentEquipmentEntities = new HashSet<>();
        BigDecimal totalSumPriceNetto = new BigDecimal(0);
        for (final RentReturnEquipmentRecordResDto eqDto : equipmentsList) {
            final BigDecimal sumPriceNetto = getRecalculatedPrice(eqDto, briefTimeData.days(), briefTimeData.allHours());

            final BigDecimal sumPriceBrutto = CurrencyUtils.addTax(taxValue, sumPriceNetto);
            final BigDecimal depositPriceBrutto = CurrencyUtils.addTax(taxValue, eqDto.depositPriceNetto());

            final EquipmentEntity equipmentEntity = session.getReference(EquipmentEntity.class, eqDto.equipmentId());
            final RentEquipmentEntity rentEquipmentEntity = session.getReference(RentEquipmentEntity.class, eqDto.id());
            final RentReturnEquipmentEntity returnEquipmentEntity = modelMapperBean
                .map(eqDto, RentReturnEquipmentEntity.class);

            equipmentDao.increaseAvailableSelectedEquipmentCount(equipmentEntity.getId(), eqDto.count());

            final var emailEquipment = new EmailEquipmentPayloadDataDto(equipmentEntity, eqDto);
            emailEquipment.setPriceNetto(sumPriceNetto);
            emailEquipment.setPriceBrutto(sumPriceBrutto);
            emailEquipment.setDepositPriceBrutto(depositPriceBrutto);
            emailEquipmentsPayload.add(emailEquipment);

            returnEquipmentEntity.setId(null);
            returnEquipmentEntity.setTotalPrice(sumPriceNetto);
            returnEquipmentEntity.setEquipment(equipmentEntity);
            returnEquipmentEntity.setRentReturn(rentReturn);
            returnEquipmentEntity.setRentEquipment(rentEquipmentEntity);
            rentEquipmentEntities.add(returnEquipmentEntity);

            totalSumPriceNetto = totalSumPriceNetto.add(sumPriceNetto);
        }
        rentReturn.setTotalPrice(totalSumPriceNetto);
        rentReturn.setEquipments(rentEquipmentEntities);
        return totalSumPriceNetto;
    }

    private BigDecimal getRecalculatedPrice(RentReturnEquipmentRecordResDto eqDto, long rentDays, long totalRentHours) {
        // price per all days calculated by per-day-price method
        final BigDecimal totalPriceDays = eqDto.pricePerDay().multiply(new BigDecimal(rentDays));
        BigDecimal totalPriceHoursSum = eqDto.pricePerHour();
        // if rent length is not full day, add rest of hours by price-per-hour for equipment
        if ((totalRentHours % 24) > 0) {
            // get N-1 hours and sum together with price per-next-hour (without first hour)
            for (int i = 0; i < (totalRentHours % 24) - 1; i++) {
                totalPriceHoursSum = totalPriceHoursSum.add(eqDto.priceForNextHour());
            }
        }
        return totalPriceDays
            .add(totalPriceHoursSum) // add to price per day price per rest hours
            .multiply(new BigDecimal(eqDto.count())); // multiply all by rent equipment count
    }

    private Attachment generatePdfReturnDocument(
        RentReturnDetailsResDto rentDetails,
        CustomerDetailsReturnResDto customerDetails,
        LocalDateTime generatedBrief,
        BriefTimeData timeData,
        String description,
        String returnIssuerIdentifier,
        RentEmailPayloadDataDto emailPayload
    ) {
        final PdfDocumentData pdfDocumentData = modelMapperBean.map(rentDetails, PdfDocumentData.class);
        modelMapperBean.map(rentDetails, pdfDocumentData.getPriceUnits());
        modelMapperBean.map(customerDetails, pdfDocumentData);

        pdfDocumentData.setIssuedIdentifier(returnIssuerIdentifier);
        pdfDocumentData.setReturnDate(DateUtils.toISO8601Format(generatedBrief.toString()));
        pdfDocumentData.setRentTime(DateUtils.paraphraseDayAndHoursSentence(timeData));
        pdfDocumentData.setDescription(description);

        final PriceUnitsDto pdfPrices = pdfDocumentData.getPriceUnits();
        pdfPrices.setTotalPriceNetto(emailPayload.getTotalPriceNetto());
        pdfPrices.setTotalPriceBrutto(emailPayload.getTotalPriceBrutto());

        final BigDecimal totalSumPrice = emailPayload.getTotalPriceNetto().add(pdfPrices.getTotalDepositPriceNetto());
        pdfDocumentData.setTotalSumPriceNetto(totalSumPrice.toString());
        pdfDocumentData.setTotalSumPriceBrutto(emailPayload.getTotalPriceWithDepositBrutto().toString());

        final List<PdfEquipmentDataDto> pdfEquipmentsData = modelMapperBean.map(emailPayload.getRentEquipments(),
            new TypeToken<List<PdfEquipmentDataDto>>() { }.getType());
        pdfDocumentData.setEquipments(pdfEquipmentsData);

        final ReturnPdfDocumentGenerator returnPdfDocument = new ReturnPdfDocumentGenerator();
        final GeneratedPdfData generatedPdfData = returnPdfDocument.generate(pdfDocumentData);
        return new Attachment(generatedPdfData.filename(), generatedPdfData.pdfData(), generatedPdfData.type());
    }

    private RentEmailPayloadDataDto generateEmailPayload(
        RentReturnDetailsResDto rentDetails,
        CustomerDetailsReturnResDto customerDetails,
        BigDecimal totalSumPriceNetto,
        LocalDateTime generatedBrief,
        BriefTimeData briefTimeData,
        Set<EmailEquipmentPayloadDataDto> emailEquipmentsPayload
    ) {
        final RentEmailPayloadDataDto emailPayload = modelMapperBean.map(rentDetails, RentEmailPayloadDataDto.class);
        modelMapperBean.map(customerDetails, emailPayload);
        // calculate brutto total price
        final BigDecimal totalSumPriceBrutto = CurrencyUtils.addTax(emailPayload.getTax(), totalSumPriceNetto);

        emailPayload.setReturnDate(generatedBrief.toString());
        emailPayload.setTotalPriceNetto(totalSumPriceNetto);
        emailPayload.setRentTime(DateUtils.paraphraseDayAndHoursSentence(briefTimeData));
        emailPayload.setTotalPriceBrutto(totalSumPriceBrutto);

        final BigDecimal totalWithTax = totalSumPriceBrutto.add(rentDetails.totalDepositPriceBrutto());
        emailPayload.setTotalPriceWithDepositBrutto(totalWithTax);
        emailPayload.getRentEquipments().addAll(emailEquipmentsPayload);

        return emailPayload;
    }

    private void sendReturnEmailMessages(
        Session session,
        RentEmailPayloadDataDto emailPayload,
        String description,
        RentReturnEntity rentReturn,
        RentReturnDetailsResDto rentDetails,
        WebServletRequest req,
        Attachment pdfDocument,
        LoggedUserDataDto loggedUser
    ) {
        final Map<String, Object> templateVars = new HashMap<>();
        templateVars.put("rentIdentifier", rentDetails.issuedIdentifier());
        templateVars.put("returnIdentifier", rentReturn.getIssuedIdentifier());
        templateVars.put("additionalDescription", description == null ? "<i>Brak danych</i>" : description);
        templateVars.put("data", emailPayload);

        final MailRequestPayload mailRequestPayload = MailRequestPayload.builder()
            .subject("Nowy zwrot: " + rentReturn.getIssuedIdentifier())
            .templateVars(templateVars)
            .attachments(List.of(pdfDocument))
            .build();

        // send to employer
        mailRequestPayload.setMessageResponder(loggedUser.getFullName());
        mailRequestPayload.setTemplate(MailTemplate.CREATE_NEW_RETURN_EMPLOYER);
        mailServiceBean.sendMessage(loggedUser.getEmailAddress(), mailRequestPayload, req);
        log.info("Successful send rent-return email message for employer. Payload: {}", mailRequestPayload);

        // send to customer
        mailRequestPayload.setMessageResponder(emailPayload.getFullName());
        mailRequestPayload.setTemplate(MailTemplate.CREATE_NEW_RETURN_CUSTOMER);
        mailServiceBean.sendMessage(emailPayload.getEmail(), mailRequestPayload, req);
        log.info("Successful send rent-return email message for customer. Payload: {}", mailRequestPayload);

        // send to owner(s)
        final Map<String, Object> ownerTemplateVars = new HashMap<>(templateVars);
        ownerTemplateVars.put("employerFullName", loggedUser.getFullName());

        final EmployerDao employerDao = new EmployerDaoHib(session);
        mailRequestPayload.setTemplate(MailTemplate.CREATE_NEW_RETURN_OWNER);
        mailRequestPayload.setTemplateVars(ownerTemplateVars);

        for (final OwnerMailPayloadDto owner : employerDao.findAllEmployersMailSenders()) {
            mailRequestPayload.setMessageResponder(owner.fullName());
            mailServiceBean.sendMessage(owner.email(), mailRequestPayload, req);
        }
        log.info("Successful send rent-return email message for owner/owners. Payload: {}", mailRequestPayload);
    }
}
