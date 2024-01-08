/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.domain.seller.deliv_return;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import pl.polsl.skirentalservice.core.ConfigSingleton;
import pl.polsl.skirentalservice.core.ModelMapperGenerator;
import pl.polsl.skirentalservice.core.db.HibernateDbSingleton;
import pl.polsl.skirentalservice.core.mail.Attachment;
import pl.polsl.skirentalservice.core.mail.MailRequestPayload;
import pl.polsl.skirentalservice.core.mail.MailSocketSingleton;
import pl.polsl.skirentalservice.dao.*;
import pl.polsl.skirentalservice.dao.hibernate.*;
import pl.polsl.skirentalservice.dto.*;
import pl.polsl.skirentalservice.dto.deliv_return.CustomerDetailsReturnResDto;
import pl.polsl.skirentalservice.dto.deliv_return.RentReturnDetailsResDto;
import pl.polsl.skirentalservice.dto.deliv_return.RentReturnEquipmentRecordResDto;
import pl.polsl.skirentalservice.dto.deliv_return.ReturnAlreadyExistPayloadDto;
import pl.polsl.skirentalservice.dto.login.LoggedUserDataDto;
import pl.polsl.skirentalservice.entity.*;
import pl.polsl.skirentalservice.pdf.ReturnPdfDocument;
import pl.polsl.skirentalservice.pdf.dto.PdfEquipmentDataDto;
import pl.polsl.skirentalservice.pdf.dto.ReturnPdfDocumentDataDto;
import pl.polsl.skirentalservice.util.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static pl.polsl.skirentalservice.exception.AlreadyExistException.ReturnDocumentAlreadyExistException;
import static pl.polsl.skirentalservice.exception.DateException.ReturnDateBeforeRentDateException;
import static pl.polsl.skirentalservice.exception.NotFoundException.*;

@Slf4j
@WebServlet("/seller/generate-return")
public class SellerGenerateReturnServlet extends HttpServlet {
    private final ModelMapper modelMapper = ModelMapperGenerator.getModelMapper();

    private final SessionFactory sessionFactory = HibernateDbSingleton.getInstance().getSessionFactory();
    private final MailSocketSingleton mailSocket = MailSocketSingleton.getInstance();
    private final ConfigSingleton config = ConfigSingleton.getInstance();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final String rentId = req.getParameter("rentId");
        final String description = StringUtils.trimToNull(req.getParameter("description"));

        final AlertTupleDto alert = new AlertTupleDto(true);
        final HttpSession httpSession = req.getSession();
        final var userDataDto = (LoggedUserDataDto) httpSession
            .getAttribute(SessionAttribute.LOGGED_USER_DETAILS.getName());

        try (final Session session = sessionFactory.openSession()) {
            try {
                session.beginTransaction();

                final EmployerDao employerDao = new EmployerDaoHib(session);
                final EquipmentDao equipmentDao = new EquipmentDaoHib(session);
                final CustomerDao customerDao = new CustomerDaoHib(session);
                final RentDao rentDao = new RentDaoHib(session);
                final ReturnDao returnDao = new ReturnDaoHib(session);

                if (!customerDao.checkIfCustomerExist(rentId)) {
                    throw new RuntimeException("Generowanie zwrotu wypożyczenia z usuniętym klientem jest niemożliwe.");
                }

                final Optional<ReturnAlreadyExistPayloadDto> returnExist = returnDao.findReturnExistDocument(rentId);
                if (returnExist.isPresent()) {
                    final ReturnAlreadyExistPayloadDto returnData = returnExist.get();
                    throw new ReturnDocumentAlreadyExistException(req, returnData.returnIdentifier(), returnData.id());
                }

                final RentReturnDetailsResDto rentDetails = rentDao.findRentReturnDetails(rentId, userDataDto.getId())
                    .orElseThrow(RentNotFoundException::new);

                final List<RentReturnEquipmentRecordResDto> equipmentsList = equipmentDao
                    .findAllEquipmentsConnectedWithRentReturn(rentId);
                if (equipmentsList.isEmpty()) throw new EquipmentNotFoundException();

                final LocalDateTime generatedBrief = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
                final String returnIssuerIdentifier = rentDetails.issuedIdentifier().replace("WY", "ZW");
                if (rentDetails.rentDateTime().isAfter(generatedBrief)) {
                    throw new ReturnDateBeforeRentDateException();
                }

                final LocalDateTime startTruncated = Utils.truncateToTotalHour(rentDetails.rentDateTime());
                final LocalDateTime endTruncated = Utils.truncateToTotalHour(generatedBrief);
                final long totalRentHours = Duration.between(startTruncated, endTruncated).toHours();
                final long rentDays = totalRentHours / 24;

                final RentEntity rentEntity = session.get(RentEntity.class, rentId);
                final RentReturnEntity rentReturn = modelMapper.map(rentDetails, RentReturnEntity.class);
                rentReturn.setTotalDepositPrice(rentDetails.totalDepositPriceNetto());
                final Set<RentReturnEquipmentEntity> rentEquipmentEntities = new HashSet<>();
                final Set<EmailEquipmentPayloadDataDto> emailEquipmentsPayload = new HashSet<>();

                rentReturn.setEquipments(new HashSet<>());
                BigDecimal totalSumPriceNetto = new BigDecimal(0);

                for (final RentReturnEquipmentRecordResDto eqDto : equipmentsList) {
                    final BigDecimal sumPriceNetto = getSumPriceNetto(eqDto, rentDays, totalRentHours);

                    final BigDecimal taxValue = new BigDecimal(rentDetails.tax());
                    final BigDecimal sumPriceBrutto = taxValue
                        .divide(new BigDecimal(100), 2, RoundingMode.HALF_UP).multiply(sumPriceNetto).add(sumPriceNetto)
                        .setScale(2, RoundingMode.HALF_UP);

                    final BigDecimal depositPriceBrutto = taxValue
                        .divide(new BigDecimal(100), 2, RoundingMode.HALF_UP).multiply(eqDto.depositPriceNetto())
                        .add(eqDto.depositPriceNetto())
                        .setScale(2, RoundingMode.HALF_UP);

                    final EquipmentEntity equipmentEntity = session
                        .getReference(EquipmentEntity.class, eqDto.equipmentId());
                    final RentEquipmentEntity rentEquipmentEntity = session
                        .getReference(RentEquipmentEntity.class, eqDto.id());
                    final RentReturnEquipmentEntity returnEquipmentEntity = modelMapper
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
                rentReturn.setIssuedIdentifier(returnIssuerIdentifier);
                rentReturn.setIssuedDateTime(generatedBrief);
                rentReturn.setDescription(description);
                rentReturn.setTotalPrice(totalSumPriceNetto);
                rentReturn.setEquipments(rentEquipmentEntities);
                rentReturn.setRent(rentEntity);

                rentDao.updateRentStatus(RentStatus.RETURNED, rentId);

                final CustomerDetailsReturnResDto customerDetails = customerDao.findCustomerDetailsForReturnDocument(rentId)
                    .orElseThrow(() -> new UserNotFoundException(UserRole.USER));

                final var emailPayload = modelMapper.map(rentDetails, RentReturnEmailPayloadDataDto.class);
                modelMapper.map(customerDetails, emailPayload);

                final BigDecimal totalSumPriceBrutto = new BigDecimal(emailPayload.getTax())
                    .divide(new BigDecimal(100), 2, RoundingMode.HALF_UP).multiply(totalSumPriceNetto).add(totalSumPriceNetto)
                    .setScale(2, RoundingMode.HALF_UP);
                emailPayload.setReturnDate(generatedBrief.toString());
                emailPayload.setTotalPriceNetto(totalSumPriceNetto);
                emailPayload.setRentTime(rentDays + " dni, " + totalRentHours + " godzin");
                emailPayload.setTotalPriceBrutto(totalSumPriceBrutto);

                final BigDecimal totalWithTax = totalSumPriceBrutto.add(rentDetails.totalDepositPriceBrutto());
                emailPayload.setTotalPriceWithDepositBrutto(totalWithTax);
                emailPayload.getRentEquipments().addAll(emailEquipmentsPayload);

                final String emailTopic = "Nowy zwrot: " + rentReturn.getIssuedIdentifier();
                final Map<String, Object> templateVars = new HashMap<>();
                templateVars.put("rentIdentifier", rentDetails.issuedIdentifier());
                templateVars.put("returnIdentifier", rentReturn.getIssuedIdentifier());
                templateVars.put("additionalDescription", description == null ? "<i>Brak danych</i>" : description);
                templateVars.put("data", emailPayload);

                final ReturnPdfDocumentDataDto returnPdfDataDto = modelMapper.map(rentDetails, ReturnPdfDocumentDataDto.class);
                modelMapper.map(rentDetails, returnPdfDataDto.getPriceUnits());
                modelMapper.map(customerDetails, returnPdfDataDto);

                returnPdfDataDto.setIssuedIdentifier(returnIssuerIdentifier);
                returnPdfDataDto.setReturnDate(generatedBrief.toString());
                returnPdfDataDto.setRentTime(rentDays + " dni, " + totalRentHours + " godzin");
                returnPdfDataDto.setDescription(description);

                final PriceUnitsDto pdfPrices = returnPdfDataDto.getPriceUnits();
                pdfPrices.setTotalPriceNetto(totalSumPriceNetto);
                pdfPrices.setTotalPriceBrutto(totalSumPriceBrutto);

                returnPdfDataDto.setTotalSumPriceNetto(totalSumPriceNetto.add(pdfPrices.getTotalDepositPriceNetto()).toString());
                returnPdfDataDto.setTotalSumPriceBrutto(totalWithTax.toString());

                final List<PdfEquipmentDataDto> pdfEquipmentsData = modelMapper.map(emailEquipmentsPayload,
                    new TypeToken<List<PdfEquipmentDataDto>>() { }.getType());
                returnPdfDataDto.setEquipments(pdfEquipmentsData);

                final ReturnPdfDocument returnPdfDocument = new ReturnPdfDocument(config.getUploadsDir(), returnPdfDataDto);
                returnPdfDocument.generate();

                final MailRequestPayload customerPayload = MailRequestPayload.builder()
                    .messageResponder(emailPayload.getFullName())
                    .subject(emailTopic)
                    .template(MailTemplate.CREATE_NEW_RETURN_CUSTOMER)
                    .templateVars(templateVars)
                    .attachments(List.of(new Attachment(data.filename(), data.pdfData(), data.type())))
                    .build();
                mailSocket.sendMessage(emailPayload.getEmail(), customerPayload, req);
                log.info("Successful send rent-return email message for customer. Payload: {}", customerPayload);

                final MailRequestPayload employerPayload = MailRequestPayload.builder()
                    .messageResponder(userDataDto.getFullName())
                    .subject(emailTopic)
                    .template(MailTemplate.CREATE_NEW_RETURN_EMPLOYER)
                    .templateVars(templateVars)
                    .attachments(List.of(new Attachment(data.filename(), data.pdfData(), data.type())))
                    .build();
                mailSocket.sendMessage(userDataDto.getEmailAddress(), employerPayload, req);
                log.info("Successful send rent-return email message for employer. Payload: {}", employerPayload);

                final Map<String, Object> ownerTemplateVars = new HashMap<>(templateVars);
                ownerTemplateVars.put("employerFullName", userDataDto.getFullName());

                final MailRequestPayload ownerPayload = MailRequestPayload.builder()
                    .subject(emailTopic)
                    .template(MailTemplate.CREATE_NEW_RETURN_OWNER)
                    .templateVars(ownerTemplateVars)
                    .attachments(List.of(new Attachment(data.filename(), data.pdfData(), data.type())))
                    .build();
                for (final OwnerMailPayloadDto owner : employerDao.findAllEmployersMailSenders()) {
                    ownerPayload.setMessageResponder(owner.fullName());
                    mailSocket.sendMessage(owner.email(), ownerPayload, req);
                }
                log.info("Successful send rent-return email message for owner/owners. Payload: {}", ownerPayload);

                session.persist(rentReturn);
                session.getTransaction().commit();
                alert.setType(AlertType.INFO);
                alert.setMessage(
                    "Generowanie zwrotu o numerze <strong>" + returnIssuerIdentifier + "</strong> dla wypożyczenia o " +
                        "numerze <strong>" + rentDetails.issuedIdentifier() + "</strong> zakończone sukcesem. Potwierdzenie " +
                        "wraz z podsumowaniem zostało wysłane również na adres email."
                );
                httpSession.setAttribute(SessionAlert.COMMON_RETURNS_PAGE_ALERT.getName(), alert);
                res.sendRedirect("/seller/returns");
            } catch (RuntimeException ex) {
                Utils.onHibernateException(session, log, ex);
            }
        } catch (RuntimeException ex) {
            alert.setMessage(ex.getMessage());
            httpSession.setAttribute(SessionAlert.COMMON_RENTS_PAGE_ALERT.getName(), alert);
            res.sendRedirect("/seller/rents");
        }
    }

    private BigDecimal getSumPriceNetto(RentReturnEquipmentRecordResDto eqDto, long rentDays, long totalRentHours) {
        final BigDecimal totalPriceDays = eqDto.pricePerDay().multiply(new BigDecimal(rentDays));
        BigDecimal totalPriceHoursSum = eqDto.pricePerHour();
        if ((totalRentHours % 24) > 0) {
            for (int i = 0; i < (totalRentHours % 24) - 1; i++) {
                totalPriceHoursSum = totalPriceHoursSum.add(eqDto.priceForNextHour());
            }
        }
        final BigDecimal totalPrice = totalPriceDays.add(totalPriceHoursSum);
        return totalPrice.multiply(new BigDecimal(eqDto.count()));
    }
}
