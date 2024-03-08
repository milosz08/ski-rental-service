/*
 * Copyright (c) 2024 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.service.impl;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
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
import pl.polsl.skirentalservice.dao.CustomerDao;
import pl.polsl.skirentalservice.dao.EmployerDao;
import pl.polsl.skirentalservice.dao.EquipmentDao;
import pl.polsl.skirentalservice.dao.RentDao;
import pl.polsl.skirentalservice.dao.hibernate.CustomerDaoHib;
import pl.polsl.skirentalservice.dao.hibernate.EmployerDaoHib;
import pl.polsl.skirentalservice.dao.hibernate.EquipmentDaoHib;
import pl.polsl.skirentalservice.dao.hibernate.RentDaoHib;
import pl.polsl.skirentalservice.dto.*;
import pl.polsl.skirentalservice.dto.customer.CustomerDetailsResDto;
import pl.polsl.skirentalservice.dto.employer.EmployerDetailsResDto;
import pl.polsl.skirentalservice.dto.login.LoggedUserDataDto;
import pl.polsl.skirentalservice.dto.rent.*;
import pl.polsl.skirentalservice.entity.*;
import pl.polsl.skirentalservice.exception.AlreadyExistException;
import pl.polsl.skirentalservice.exception.DateException;
import pl.polsl.skirentalservice.exception.NotFoundException;
import pl.polsl.skirentalservice.exception.RentException;
import pl.polsl.skirentalservice.pdf.RentPdfDocumentGenerator;
import pl.polsl.skirentalservice.pdf.dto.GeneratedPdfData;
import pl.polsl.skirentalservice.pdf.dto.PdfDocumentData;
import pl.polsl.skirentalservice.service.RentService;
import pl.polsl.skirentalservice.util.CurrencyUtils;
import pl.polsl.skirentalservice.util.DateUtils;
import pl.polsl.skirentalservice.util.RentStatus;
import pl.polsl.skirentalservice.util.UserRole;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Stateless
@SuppressWarnings("unused")
public class RentServiceBean implements RentService {
    private final PersistenceBean persistenceBean;
    private final S3ClientBean s3ClientBean;
    private final ModelMapperBean modelMapperBean;
    private final MailServiceBean mailServiceBean;

    @Inject
    public RentServiceBean(
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
    public Slice<OwnerRentRecordResDto> getPageableOwnerRents(PageableDto pageableDto) {
        return persistenceBean.startNonTransactQuery(session -> {
            final RentDao rentDao = new RentDaoHib(session);
            final Long totalRents = rentDao.findAllRentsCount(pageableDto.filterData());

            final ServletPagination pagination = new ServletPagination(pageableDto.page(),
                pageableDto.total(), totalRents);
            if (pagination.checkIfIsInvalid()) {
                return new Slice<>(pagination);
            }
            final List<OwnerRentRecordResDto> rentsList = rentDao.findAllPageableRents(pageableDto);
            return new Slice<>(pagination, rentsList);
        });
    }

    @Override
    public Slice<SellerRentRecordResDto> getPageableEmployerRents(PageableDto pageableDto, Long employerId) {
        return persistenceBean.startNonTransactQuery(session -> {
            final RentDao rentDao = new RentDaoHib(session);
            final Long totalRents = rentDao.findAllRentsFromEmployerCount(pageableDto.filterData(), employerId);

            final ServletPagination pagination = new ServletPagination(pageableDto.page(),
                pageableDto.total(), totalRents);
            if (pagination.checkIfIsInvalid()) {
                return new Slice<>(pagination);
            }
            final List<SellerRentRecordResDto> rentsList = rentDao.findAllPageableRentsFromEmployer(
                pageableDto, employerId);

            return new Slice<>(pagination, rentsList);
        });
    }

    @Override
    public Slice<EquipmentRentRecordResDto> getPageableRentEquipments(PageableDto pageableDto) {
        return persistenceBean.startNonTransactQuery(session -> {
            final EquipmentDao equipmentDao = new EquipmentDaoHib(session);
            final Long totalEquipments = equipmentDao.findAllEquipmentsCount(pageableDto.filterData());

            final ServletPagination pagination = new ServletPagination(pageableDto.page(),
                pageableDto.total(), totalEquipments);
            if (pagination.checkIfIsInvalid()) {
                return new Slice<>(pagination);
            }
            final List<EquipmentRentRecordResDto> equipmentsList = equipmentDao
                .findAllPageableEquipments(pageableDto)
                .stream()
                .filter(l -> l.getTotalCount() > 0)
                .toList();
            return new Slice<>(pagination, equipmentsList);
        });
    }

    @Override
    public MultipleEquipmentsDataDto<RentDetailsResDto> getRentDetails(Long rentId, LoggedUserDataDto loggedUser) {
        return persistenceBean.startNonTransactQuery(session -> {
            final RentDao rentDao = new RentDaoHib(session);
            final EquipmentDao equipmentDao = new EquipmentDaoHib(session);

            final RentDetailsResDto rentDetails = rentDao
                .findRentDetails(rentId, loggedUser.getId(), String.valueOf(loggedUser.getRoleAlias()))
                .orElseThrow(NotFoundException.RentNotFoundException::new);

            final List<RentEquipmentsDetailsResDto> allRentEquipments = equipmentDao
                .findAllEquipmentsConnectedWithRent(rentId);

            final Integer totalSum = allRentEquipments
                .stream()
                .map(RentEquipmentsDetailsResDto::count)
                .reduce(0, Integer::sum);

            return new MultipleEquipmentsDataDto<>(rentDetails, allRentEquipments, totalSum);
        });
    }

    @Override
    public void calculatePricesForRentEquipments(
        List<EquipmentRentRecordResDto> equipments, InMemoryRentDataDto rentData, AddEditEquipmentCartResDto editModal
    ) {
        // get start and end renting time total hours (scale up)
        final BriefTimeData briefTimeData = DateUtils
            .getHoursAndDaysFromBriefTime(rentData.getParsedRentDateTime(), rentData.getParsedReturnDateTime());

        rentData.setDays(briefTimeData.days());
        rentData.setHours(briefTimeData.allHours() % 24);

        final PriceUnitsDto priceUnits = new PriceUnitsDto();
        int totalCounts = 0; // all equipments count

        for (final CartSingleEquipmentDataDto cartDto : rentData.getEquipments()) {
            // find matching equipment base cart in-memory data
            final EquipmentRentRecordResDto recordDto = equipments.stream()
                .filter(e -> e.getId().equals(cartDto.getId())).findFirst()
                .orElseThrow(() -> new NotFoundException.EquipmentNotFoundException(cartDto.getId().toString()));

            recordDto.setDisabled(true);

            final BigDecimal totalPrice = getTotalPrice(recordDto, briefTimeData.days(), briefTimeData.allHours());
            final BigDecimal sumPriceNetto = totalPrice.multiply(new BigDecimal(cartDto.getCount()));
            final BigDecimal sumPriceBrutto = CurrencyUtils.addTax(rentData.getTax(), sumPriceNetto);
            final BigDecimal depositPriceNetto = cartDto.getPriceUnits().getTotalDepositPriceNetto();
            final BigDecimal depositPriceBrutto = CurrencyUtils.addTax(rentData.getTax(), depositPriceNetto);

            if (editModal != null && Long.parseLong(editModal.getEqId()) == recordDto.getId()) {
                cartDto.setResDto(editModal);
            }
            cartDto.setName(recordDto.getName());
            cartDto.setPrices(sumPriceNetto, sumPriceBrutto, depositPriceBrutto);
            // cummulate all equipments count
            totalCounts += Integer.parseInt(cartDto.getCount());
            priceUnits.add(sumPriceNetto, sumPriceBrutto, depositPriceNetto, depositPriceBrutto);
        }
        rentData.setTotalCount(totalCounts);
        rentData.setPriceUnits(priceUnits);
        rentData.getEquipments().sort(Comparator.comparing(CartSingleEquipmentDataDto::getId));
    }

    @Override
    public String deleteRent(Long rentId, LoggedUserDataDto loggedUser) {
        return persistenceBean.startTransaction(session -> {
            final EquipmentDao equipmentDao = new EquipmentDaoHib(session);
            final CustomerDao customerDao = new CustomerDaoHib(session);

            final RentEntity rentEntity = session.getReference(RentEntity.class, rentId);
            if (rentEntity == null) {
                throw new NotFoundException.RentNotFoundException();
            }
            final String rentIdentifier = rentEntity.getIssuedIdentifier();

            if (rentEntity.getStatus().equals(RentStatus.RETURNED)) {
                throw new RentException.RentHasReturnException(rentEntity);
            }
            session.remove(rentEntity);

            for (final RentEquipmentEntity equipment : rentEntity.getEquipments()) {
                if (equipment.getEquipment() != null && rentEntity.getCustomer() != null) {
                    equipmentDao.increaseAvailableSelectedEquipmentCount(equipment.getEquipment().getId(),
                        equipment.getCount());
                }
            }
            s3ClientBean.deleteObject(S3Bucket.RENTS, rentEntity.getIssuedIdentifier().replaceAll("/", "-") + ".pdf");

            session.getTransaction().commit();

            log.info("Rent with id: {} was succesfuly removed from system by {}. Rent data: {}", rentId,
                loggedUser.getLogin(), rentEntity);
            return rentIdentifier;
        });
    }

    @Override
    public boolean checkIfRentExist(Long rentId) {
        return persistenceBean.startNonTransactQuery(session -> {
            final RentDao rentDao = new RentDaoHib(session);
            return rentDao.checkIfRentExist(rentId);
        });
    }

    @Override
    public boolean checkIfRentIsFromEmployer(Long rentId, Long employerId) {
        return persistenceBean.startNonTransactQuery(session -> {
            final RentDao rentDao = new RentDaoHib(session);
            return rentDao.checkIfRentIsFromEmployer(rentId, employerId);
        });
    }

    @Override
    public UpdatedInMemoryRentData updateAndGetInMemoryRentData(Long customerId, LoggedUserDataDto loggedUser) {
        return persistenceBean.startNonTransactQuery(session -> {
            final CustomerDao customerDao = new CustomerDaoHib(session);
            final EmployerDao employerDao = new EmployerDaoHib(session);
            final EquipmentDao equipmentDao = new EquipmentDaoHib(session);
            final RentDao rentDao = new RentDaoHib(session);

            final Long isSomeEquipmentsAvaialble = equipmentDao.getCountIfSomeEquipmentsAreAvailable();
            if (isSomeEquipmentsAvaialble == null || isSomeEquipmentsAvaialble < 0) {
                return new UpdatedInMemoryRentData(true);
            }
            final CustomerDetailsResDto customerDetails = customerDao
                .findCustomerDetails(customerId)
                .orElseThrow(() -> new NotFoundException.UserNotFoundException(UserRole.USER));
            final EmployerDetailsResDto employerDetails = employerDao
                .findEmployerPageDetails(loggedUser.getId())
                .orElseThrow(() -> new NotFoundException.UserNotFoundException(UserRole.SELLER));


            return new UpdatedInMemoryRentData(false, customerDetails, employerDetails);
        });
    }

    @Override
    public void persistFirstStageRentForm(
        NewRentDetailsReqDto reqDto, NewRentDetailsResDto resDto,
        InMemoryRentDataDto rentData,
        LoggedUserDataDto loggedUser, Long customerId
    ) {
        resDto.setIssuedDateTime(rentData.getIssuedDateTime());
        if (reqDto.getParsedRentDateTime().isBefore(resDto.getParsedIssuedDateTime())) {
            throw new DateException.RentDateBeforeIssuedDateException();
        }
        if (reqDto.getParsedReturnDateTime().isBefore(reqDto.getParsedRentDateTime())) {
            throw new DateException.ReturnDateBeforeRentDateException();
        }
        rentData.setRentDateTime(DateUtils.toISO8601Format(reqDto.getRentDateTime()));
        rentData.setReturnDateTime(DateUtils.toISO8601Format(reqDto.getReturnDateTime()));
        rentData.setTax(reqDto.getTax());
        rentData.setDescription(reqDto.getDescription());
        rentData.setAllGood(true);

        resDto.setIssuedIdentifier(rentData.getIssuedIdentifier());
        resDto.setRentStatus(rentData.getRentStatus());

        log.info("Successfully persisted first stage rent form with data: {} by employer: {} for customer: {}",
            rentData, loggedUser.getLogin(), customerId);
    }

    @Override
    public void persistNewRent(InMemoryRentDataDto rentData, LoggedUserDataDto loggedUser, WebServletRequest req) {
        final AtomicReference<String> generatedFileKey = new AtomicReference<>(StringUtils.EMPTY);
        persistenceBean.startTransaction(session -> {
            final EmployerDao employerDao = new EmployerDaoHib(session);
            final EquipmentDao equipmentDao = new EquipmentDaoHib(session);

            final RentEntity rent = modelMapperBean.map(rentData, RentEntity.class);
            final RentStatus status = rentData.getParsedRentDateTime().isAfter(LocalDateTime.now())
                ? RentStatus.BOOKED : RentStatus.RENTED;

            createRentEquipmentsList(session, equipmentDao, rentData, rent);
            rent.setId(null);
            rent.setIssuedDateTime(DateUtils.fromISO8601Format(rentData.getIssuedDateTime()));
            rent.setTotalPrice(rentData.getPriceUnits().getTotalPriceNetto());
            rent.setTotalDepositPrice(rentData.getPriceUnits().getTotalDepositPriceNetto());
            rent.setStatus(status);
            rent.setCustomer(session.get(CustomerEntity.class, rentData.getCustomerId()));
            rent.setEmployer(session.get(EmployerEntity.class, loggedUser.getId()));

            session.persist(rent);

            final RentEmailPayloadDataDto emailPayload = generateEmailPayload(rentData);
            final Attachment pdfReturnDocument = generatePdfReturnDocument(rentData);
            s3ClientBean.putObject(S3Bucket.RENTS, pdfReturnDocument);
            generatedFileKey.set(pdfReturnDocument.name());

            sendRentEmailMessages(session, emailPayload, rentData, req, pdfReturnDocument, loggedUser);

            session.getTransaction().commit();
            log.info("Successfuly persist new rent by: {} in database. Rent data: {}", loggedUser, rentData);
        }, () -> {
            final String generatedFileKeyValue = generatedFileKey.get();
            if (!generatedFileKeyValue.equals(StringUtils.EMPTY)) {
                s3ClientBean.deleteObject(S3Bucket.RENTS, generatedFileKeyValue);
            }
        });
    }

    @Override
    public String generateIssuedIdentifier(Long customerId, Long employerId) {
        return persistenceBean.startNonTransactQuery(session -> {
            final RentDao rentDao = new RentDaoHib(session);

            final LocalDateTime now = LocalDateTime.now();
            final String issuerStaticPart = "WY/" + now.getYear() + "/" + now.getMonth().getValue() + "/";
            final String issuerUsers = "/" + employerId + "/" + customerId;

            String issuedIdentifier, randomizerOutput;
            do {
                randomizerOutput = RandomStringUtils.randomNumeric(4);
                issuedIdentifier = issuerStaticPart + randomizerOutput + issuerUsers;
            } while (rentDao.checkIfIssuerExist(randomizerOutput));
            return issuedIdentifier;
        });
    }

    private void createRentEquipmentsList(
        Session session, EquipmentDao equipmentDao, InMemoryRentDataDto rentData, RentEntity rent
    ) {
        final Set<RentEquipmentEntity> equipmentEntities = new HashSet<>();
        for (final CartSingleEquipmentDataDto cartData : rentData.getEquipments()) {
            final Integer eqCount = equipmentDao.findAllEquipmentsInCartCount(cartData.getId());
            if (eqCount < Integer.parseInt(cartData.getCount())) {
                throw new AlreadyExistException.TooMuchEquipmentsException();
            }
            final RentEquipmentEntity equipment = modelMapperBean.map(cartData, RentEquipmentEntity.class);
            final EquipmentEntity refEquipment = session.get(EquipmentEntity.class, cartData.getId());
            equipment.setId(null);
            equipment.setTotalPrice(cartData.getPriceUnits().getTotalPriceNetto());
            equipment.setDepositPrice(cartData.getPriceUnits().getTotalDepositPriceNetto());
            equipment.setEquipment(refEquipment);
            equipment.setRent(rent);
            equipmentEntities.add(equipment);
            equipmentDao.decreaseAvailableSelectedEquipmentCount(cartData.getId(), cartData.getCount());
        }
        rent.setEquipments(equipmentEntities);
    }

    private Attachment generatePdfReturnDocument(InMemoryRentDataDto rentData) {
        final PdfDocumentData pdfDocumentData = modelMapperBean.map(rentData, PdfDocumentData.class);

        final CustomerDetailsResDto customerDetails = rentData.getCustomerDetails();
        final PriceUnitsDto priceUnits = rentData.getPriceUnits();

        pdfDocumentData.setTotalSumPriceNetto(priceUnits.mergeWithDepositNettoPrice());
        pdfDocumentData.setTotalSumPriceBrutto(priceUnits.mergeWithDepositBruttoPrice());

        modelMapperBean.map(rentData.getCustomerDetails(), pdfDocumentData);
        pdfDocumentData.setAddress(customerDetails.address() + ", " + customerDetails.cityWithPostCode());
        pdfDocumentData.setRentTime(DateUtils.paraphraseDayAndHoursSentence(new BriefTimeData(rentData)));

        final RentPdfDocumentGenerator rentPdfDocument = new RentPdfDocumentGenerator();
        final GeneratedPdfData generatedPdfData = rentPdfDocument.generate(pdfDocumentData);
        return new Attachment(generatedPdfData.filename(), generatedPdfData.pdfData(), generatedPdfData.type());
    }

    private RentEmailPayloadDataDto generateEmailPayload(InMemoryRentDataDto rentData) {
        final RentEmailPayloadDataDto emailPayload = modelMapperBean.map(rentData, RentEmailPayloadDataDto.class);
        modelMapperBean.map(rentData.getCustomerDetails(), emailPayload);

        final PriceUnitsDto priceUnits = rentData.getPriceUnits();
        final BigDecimal totalWithTax = priceUnits.getTotalPriceBrutto().add(priceUnits.getTotalDepositPriceBrutto());
        emailPayload.setTotalPriceWithDepositBrutto(totalWithTax);
        emailPayload.setRentTime(DateUtils.paraphraseDayAndHoursSentence(new BriefTimeData(rentData)));

        for (final CartSingleEquipmentDataDto equipmentDataDto : rentData.getEquipments()) {
            final var equipment = modelMapperBean.map(equipmentDataDto, EmailEquipmentPayloadDataDto.class);
            emailPayload.getRentEquipments().add(equipment);
        }
        return emailPayload;
    }

    private void sendRentEmailMessages(
        Session session,
        RentEmailPayloadDataDto emailPayload,
        InMemoryRentDataDto rentData,
        WebServletRequest req,
        Attachment pdfDocument,
        LoggedUserDataDto loggedUser
    ) {
        final String description = rentData.getDescription();
        final Map<String, Object> templateVars = new HashMap<>();
        templateVars.put("rentIdentifier", rentData.getIssuedIdentifier());
        templateVars.put("additionalDescription", description == null ? "<i>Brak danych</i>" : description);
        templateVars.put("data", emailPayload);

        final MailRequestPayload mailRequestPayload = MailRequestPayload.builder()
            .subject("Nowe wypożyczenie: " + rentData.getIssuedIdentifier())
            .templateVars(templateVars)
            .attachments(List.of(pdfDocument))
            .build();

        // send to employer
        mailRequestPayload.setMessageResponder(loggedUser.getFullName());
        mailRequestPayload.setTemplate(MailTemplate.ADD_NEW_RENT_EMPLOYER);
        mailServiceBean.sendMessage(loggedUser.getEmailAddress(), mailRequestPayload, req);
        log.info("Successful send rent email message for employer. Payload: {}", mailRequestPayload);

        // send to customer
        mailRequestPayload.setMessageResponder(rentData.getCustomerDetails().fullName());
        mailRequestPayload.setTemplate(MailTemplate.ADD_NEW_RENT_CUSTOMER);
        mailServiceBean.sendMessage(emailPayload.getEmail(), mailRequestPayload, req);
        log.info("Successful send rent email message for customer. Payload: {}", mailRequestPayload);

        // send to owner(s)
        final Map<String, Object> ownerTemplateVars = new HashMap<>(templateVars);
        ownerTemplateVars.put("employerFullName", loggedUser.getFullName());

        final EmployerDao employerDao = new EmployerDaoHib(session);
        mailRequestPayload.setTemplate(MailTemplate.ADD_NEW_RENT_OWNER);
        mailRequestPayload.setTemplateVars(ownerTemplateVars);

        for (final OwnerMailPayloadDto owner : employerDao.findAllEmployersMailSenders()) {
            mailRequestPayload.setMessageResponder(owner.fullName());
            mailServiceBean.sendMessage(owner.email(), mailRequestPayload, req);
        }
        log.info("Successful send rent email message for owner/owners. Payload: {}", mailRequestPayload);
    }

    private BigDecimal getTotalPrice(EquipmentRentRecordResDto recordDto, long rentDays, long totalRentHours) {
        // calculate total price per hour and per day, multiply by total days
        final BigDecimal totalPriceDays = recordDto.getPricePerDay().multiply(new BigDecimal(rentDays));
        BigDecimal totalPriceHoursSum = new BigDecimal(0);
        // if rent length is not full day, add rest of hours by price-per-hour for equipment
        if ((totalRentHours % 24) > 0) {
            totalPriceHoursSum = recordDto.getPricePerHour();
            // get N-1 hours and sum together with price per-next-hour (without first hour)
            for (int i = 0; i < (totalRentHours % 24) - 1; i++) {
                totalPriceHoursSum = totalPriceHoursSum.add(recordDto.getPriceForNextHour());
            }
        }
        return totalPriceDays.add(totalPriceHoursSum);
    }
}
