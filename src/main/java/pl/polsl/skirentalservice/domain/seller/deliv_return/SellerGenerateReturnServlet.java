/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 * Silesian University of Technology
 *
 *  File name: SellerGenerateReturnServlet.java
 *  Last modified: 09/02/2023, 01:18
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.domain.seller.deliv_return;

import org.slf4j.*;
import org.hibernate.*;
import org.modelmapper.*;

import jakarta.ejb.EJB;
import jakarta.servlet.http.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;

import pl.polsl.skirentalservice.dto.*;
import pl.polsl.skirentalservice.util.*;
import pl.polsl.skirentalservice.entity.*;
import pl.polsl.skirentalservice.pdf.dto.*;
import pl.polsl.skirentalservice.dao.rent.*;
import pl.polsl.skirentalservice.core.mail.*;
import pl.polsl.skirentalservice.dao.customer.*;
import pl.polsl.skirentalservice.dao.employer.*;
import pl.polsl.skirentalservice.dao.equipment.*;
import pl.polsl.skirentalservice.core.ConfigBean;
import pl.polsl.skirentalservice.dao.return_deliv.*;
import pl.polsl.skirentalservice.dto.deliv_return.*;
import pl.polsl.skirentalservice.pdf.ReturnPdfDocument;
import pl.polsl.skirentalservice.dto.login.LoggedUserDataDto;

import java.util.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static java.util.Objects.isNull;
import static java.time.Duration.between;
import static java.math.RoundingMode.HALF_UP;
import static java.time.temporal.ChronoUnit.MINUTES;
import static org.apache.commons.lang3.StringUtils.trimToNull;

import static pl.polsl.skirentalservice.util.SessionAlert.*;
import static pl.polsl.skirentalservice.util.UserRole.USER;
import static pl.polsl.skirentalservice.util.AlertType.INFO;
import static pl.polsl.skirentalservice.util.RentStatus.RETURNED;
import static pl.polsl.skirentalservice.exception.DateException.*;
import static pl.polsl.skirentalservice.exception.NotFoundException.*;
import static pl.polsl.skirentalservice.util.Utils.truncateToTotalHour;
import static pl.polsl.skirentalservice.exception.AlreadyExistException.*;
import static pl.polsl.skirentalservice.core.db.HibernateUtil.getSessionFactory;
import static pl.polsl.skirentalservice.core.ModelMapperGenerator.getModelMapper;
import static pl.polsl.skirentalservice.util.SessionAttribute.LOGGED_USER_DETAILS;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@WebServlet("/seller/generate-return")
public class SellerGenerateReturnServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(SellerGenerateReturnServlet.class);
    private final SessionFactory sessionFactory = getSessionFactory();
    private final ModelMapper modelMapper = getModelMapper();

    @EJB private MailSocketBean mailSocket;
    @EJB private ConfigBean config;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final String rentId = req.getParameter("rentId");
        final String description = trimToNull(req.getParameter("description"));

        final AlertTupleDto alert = new AlertTupleDto(true);
        final HttpSession httpSession = req.getSession();
        final var userDataDto = (LoggedUserDataDto) httpSession.getAttribute(LOGGED_USER_DETAILS.getName());

        try (final Session session = sessionFactory.openSession()) {
            try {
                session.beginTransaction();

                final IEmployerDao employerDao = new EmployerDao(session);
                final IEquipmentDao equipmentDao = new EquipmentDao(session);
                final ICustomerDao customerDao = new CustomerDao(session);
                final IRentDao rentDao = new RentDao(session);
                final IReturnDao returnDao = new ReturnDao(session);

                if (!customerDao.checkIfCustomerExist(rentId)) {
                    throw new RuntimeException("Generowanie zwrotu wypożyczenia z usuniętym klientem jest niemożliwe.");
                }

                final Optional<ReturnAlreadyExistPayloadDto> returnExist = returnDao.findReturnExistDocument(rentId);
                if (returnExist.isPresent()) {
                    final ReturnAlreadyExistPayloadDto returnData = returnExist.get();
                    throw new ReturnDocumentAlreadyExistException(req, returnData.returnIdentifier(), returnData.id());
                }

                final RentReturnDetailsResDto rentDetails = rentDao.findRentReturnDetails(rentId, userDataDto.getId())
                    .orElseThrow(() -> { throw new RentNotFoundException(); });

                final List<RentReturnEquipmentRecordResDto> equipmentsList = equipmentDao
                    .findAllEquipmentsConnectedWithRentReturn(rentId);
                if (equipmentsList.isEmpty()) throw new EquipmentNotFoundException();

                final LocalDateTime generatedBrief = LocalDateTime.now().truncatedTo(MINUTES);
                final String returnIssuerIdentifier = rentDetails.issuedIdentifier().replace("WY", "ZW");
                if (rentDetails.rentDateTime().isAfter(generatedBrief)) {
                    throw new ReturnDateBeforeRentDateException();
                }

                final LocalDateTime startTruncated = truncateToTotalHour(rentDetails.rentDateTime());
                final LocalDateTime endTruncated = truncateToTotalHour(generatedBrief);
                final long totalRentHours = between(startTruncated, endTruncated).toHours();
                final long rentDays = totalRentHours / 24;

                final RentEntity rentEntity = session.get(RentEntity.class, rentId);
                final RentReturnEntity rentReturn = modelMapper.map(rentDetails, RentReturnEntity.class);
                rentReturn.setTotalDepositPrice(rentDetails.totalDepositPriceNetto());
                final Set<RentReturnEquipmentEntity> rentEquipmentEntities = new HashSet<>();
                final Set<EmailEquipmentPayloadDataDto> emailEquipmentsPayload = new HashSet<>();

                rentReturn.setEquipments(new HashSet<>());
                BigDecimal totalSumPriceNetto = new BigDecimal(0);

                for (final RentReturnEquipmentRecordResDto eqDto : equipmentsList) {
                    final BigDecimal totalPriceDays = eqDto.pricePerDay().multiply(new BigDecimal(rentDays));
                    BigDecimal totalPriceHoursSum = eqDto.pricePerHour();
                    if ((totalRentHours % 24) > 0) {
                        for (int i = 0; i < (totalRentHours % 24) - 1; i++) {
                            totalPriceHoursSum = totalPriceHoursSum.add(eqDto.priceForNextHour());
                        }
                    }
                    final BigDecimal totalPrice = totalPriceDays.add(totalPriceHoursSum);
                    final BigDecimal sumPriceNetto = totalPrice.multiply(new BigDecimal(eqDto.count()));

                    final BigDecimal taxValue = new BigDecimal(rentDetails.tax());
                    final BigDecimal sumPriceBrutto = taxValue
                        .divide(new BigDecimal(100), 2, HALF_UP).multiply(sumPriceNetto).add(sumPriceNetto)
                        .setScale(2, HALF_UP);

                    final BigDecimal depositPriceBrutto = taxValue
                        .divide(new BigDecimal(100), 2, HALF_UP).multiply(eqDto.depositPriceNetto())
                        .add(eqDto.depositPriceNetto())
                        .setScale(2, HALF_UP);

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

                rentDao.updateRentStatus(RETURNED, rentId);

                final CustomerDetailsReturnResDto customerDetails = customerDao.findCustomerDetailsForReturnDocument(rentId)
                    .orElseThrow(() -> { throw new UserNotFoundException(USER); });

                final var emailPayload = modelMapper.map(rentDetails, RentReturnEmailPayloadDataDto.class);
                modelMapper.map(customerDetails, emailPayload);

                final BigDecimal totalSumPriceBrutto = new BigDecimal(emailPayload.getTax())
                    .divide(new BigDecimal(100), 2, HALF_UP).multiply(totalSumPriceNetto).add(totalSumPriceNetto)
                    .setScale(2, HALF_UP);
                emailPayload.setReturnDate(generatedBrief.toString());
                emailPayload.setTotalPriceNetto(totalSumPriceNetto);
                emailPayload.setRentTime(rentDays +  " dni, " + totalRentHours + " godzin");
                emailPayload.setTotalPriceBrutto(totalSumPriceBrutto);

                final BigDecimal totalWithTax = totalSumPriceBrutto.add(rentDetails.totalDepositPriceBrutto());
                emailPayload.setTotalPriceWithDepositBrutto(totalWithTax);
                emailPayload.getRentEquipments().addAll(emailEquipmentsPayload);

                final String emailTopic = "SkiRent Service | Nowy zwrot: " + rentReturn.getIssuedIdentifier();
                final Map<String, Object> templateVars = new HashMap<>();
                templateVars.put("rentIdentifier", rentDetails.issuedIdentifier());
                templateVars.put("returnIdentifier", rentReturn.getIssuedIdentifier());
                templateVars.put("additionalDescription", isNull(description) ? "<i>Brak danych</i>" : description);
                templateVars.put("data", emailPayload);

                final ReturnPdfDocumentDataDto returnPdfDataDto = modelMapper.map(rentDetails, ReturnPdfDocumentDataDto.class);
                modelMapper.map(rentDetails, returnPdfDataDto.getPriceUnits());
                modelMapper.map(customerDetails, returnPdfDataDto);

                returnPdfDataDto.setIssuedIdentifier(returnIssuerIdentifier);
                returnPdfDataDto.setReturnDate(generatedBrief.toString());
                returnPdfDataDto.setRentTime(rentDays +  " dni, " + totalRentHours + " godzin");
                returnPdfDataDto.setDescription(description);

                final PriceUnitsDto pdfPrices = returnPdfDataDto.getPriceUnits();
                pdfPrices.setTotalPriceNetto(totalSumPriceNetto);
                pdfPrices.setTotalPriceBrutto(totalSumPriceBrutto);

                returnPdfDataDto.setTotalSumPriceNetto(totalSumPriceNetto.add(pdfPrices.getTotalDepositPriceNetto()).toString());
                returnPdfDataDto.setTotalSumPriceBrutto(totalWithTax.toString());

                final List<PdfEquipmentDataDto> pdfEquipmentsData = modelMapper.map(emailEquipmentsPayload,
                    new TypeToken<List<PdfEquipmentDataDto>>(){}.getType());
                returnPdfDataDto.setEquipments(pdfEquipmentsData);

                final ReturnPdfDocument returnPdfDocument = new ReturnPdfDocument(config.getUploadsDir(), returnPdfDataDto);
                returnPdfDocument.generate();

                final MailRequestPayload customerPayload = MailRequestPayload.builder()
                    .messageResponder(emailPayload.getFullName())
                    .subject(emailTopic)
                    .templateName("create-new-return-customer.template.ftl")
                    .templateVars(templateVars)
                    .attachmentsPaths(Set.of(returnPdfDocument.getPath()))
                    .build();
                mailSocket.sendMessage(emailPayload.getEmail(), customerPayload, req);
                LOGGER.info("Successful send rent-return email message for customer. Payload: {}", customerPayload);

                final MailRequestPayload employerPayload = MailRequestPayload.builder()
                    .messageResponder(userDataDto.getFullName())
                    .subject(emailTopic)
                    .templateName("create-new-return-employer.template.ftl")
                    .templateVars(templateVars)
                    .attachmentsPaths(Set.of(returnPdfDocument.getPath()))
                    .build();
                mailSocket.sendMessage(userDataDto.getEmailAddress(), employerPayload, req);
                LOGGER.info("Successful send rent-return email message for employer. Payload: {}", employerPayload);

                final Map<String, Object> ownerTemplateVars = new HashMap<>(templateVars);
                ownerTemplateVars.put("employerFullName", userDataDto.getFullName());

                final MailRequestPayload ownerPayload = MailRequestPayload.builder()
                    .subject(emailTopic)
                    .templateName("create-new-return-owner.template.ftl")
                    .templateVars(ownerTemplateVars)
                    .attachmentsPaths(Set.of(returnPdfDocument.getPath()))
                    .build();
                for (final OwnerMailPayloadDto owner : employerDao.findAllEmployersMailSenders()) {
                    ownerPayload.setMessageResponder(owner.fullName());
                    mailSocket.sendMessage(owner.email(), ownerPayload, req);
                }
                LOGGER.info("Successful send rent-return email message for owner/owners. Payload: {}", ownerPayload);

                session.persist(rentReturn);
                session.getTransaction().commit();
                alert.setType(INFO);
                alert.setMessage(
                    "Generowanie zwrotu o numerze <strong>" + returnIssuerIdentifier + "</strong> dla wypożyczenia o " +
                    "numerze <strong>" + rentDetails.issuedIdentifier() + "</strong> zakończone sukcesem. Potwierdzenie " +
                    "wraz z podsumowaniem zostało wysłane również na adres email."
                );
                httpSession.setAttribute(COMMON_RETURNS_PAGE_ALERT.getName(), alert);
                res.sendRedirect("/seller/returns");
            } catch (RuntimeException ex) {
                Utils.onHibernateException(session, LOGGER, ex);
            }
        } catch (RuntimeException ex) {
            alert.setMessage(ex.getMessage());
            httpSession.setAttribute(COMMON_RENTS_PAGE_ALERT.getName(), alert);
            res.sendRedirect("/seller/rents");
        }
    }
}
