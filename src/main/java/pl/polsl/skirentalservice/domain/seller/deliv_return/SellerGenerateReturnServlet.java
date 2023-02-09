/*
 * Copyright (c) 2023 by multiple authors
 * Silesian University of Technology
 *
 *  File name: SellerGenerateReturnServlet.java
 *  Last modified: 29/01/2023, 22:31
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
import pl.polsl.skirentalservice.core.mail.*;
import pl.polsl.skirentalservice.dto.deliv_return.*;
import pl.polsl.skirentalservice.core.ModelMapperBean;
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

                final String jpqlFindCustomerExist =
                    "SELECT c.id FROM RentEntity r INNER JOIN r.customer c WHERE r.id = :rid";
                final Long customerExist = session.createQuery(jpqlFindCustomerExist, Long.class)
                    .setParameter("rid", rentId).getSingleResultOrNull();
                if (isNull(customerExist)) throw new RuntimeException(
                    "Generowanie zwrotu wypożyczenia z usuniętym klientem jest niemożliwe."
                );
                final String jpqlFindReturn =
                    "SELECT new pl.polsl.skirentalservice.dto.deliv_return.ReturnAlreadyExistPayloadDto(" +
                        "re.id, re.issuedIdentifier" +
                    ") FROM RentReturnEntity re INNER JOIN re.rent r WHERE r.id = :rid";
                final ReturnAlreadyExistPayloadDto returnAlreadyExist = session
                    .createQuery(jpqlFindReturn, ReturnAlreadyExistPayloadDto.class)
                    .setParameter("rid", rentId).getSingleResultOrNull();
                if (!isNull(returnAlreadyExist)) {
                    throw new ReturnDocumentAlreadyExistException(req, returnAlreadyExist.getReturnIdentifier(),
                        returnAlreadyExist.getId());
                }

                final String jpqlFindRent =
                    "SELECT new pl.polsl.skirentalservice.dto.deliv_return.RentReturnDetailsResDto(" +
                        "r.issuedIdentifier, r.rentDateTime, r.tax," +
                        "r.totalPrice, CAST((r.tax / 100) * r.totalPrice + r.totalPrice AS bigdecimal)," +
                        "r.totalDepositPrice, CAST((r.tax / 100) * r.totalDepositPrice + r.totalDepositPrice AS bigdecimal)" +
                    ") FROM RentEntity r INNER JOIN r.employer e " +
                    "WHERE r.id = :rentid AND e.id = :eid";
                final RentReturnDetailsResDto rentDetails = session.createQuery(jpqlFindRent, RentReturnDetailsResDto.class)
                    .setParameter("rentid", rentId)
                    .setParameter("eid", userDataDto.getId())
                    .getSingleResultOrNull();
                if (isNull(rentDetails)) throw new RentNotFoundException();

                final String jpqlFindAllEquipmentsConnectedWithRent =
                    "SELECT new pl.polsl.skirentalservice.dto.deliv_return.RentReturnEquipmentRecordResDto(" +
                        "re.id, red.pricePerHour, red.priceForNextHour, red.pricePerDay, re.count," +
                        "re.depositPrice, red.id, re.description" +
                    ") FROM RentEquipmentEntity re " +
                    "INNER JOIN re.rent r INNER JOIN r.employer e INNER JOIN re.equipment red " +
                    "WHERE r.id = :rentid AND e.id = :eid";
                final List<RentReturnEquipmentRecordResDto> equipmentsList = session
                    .createQuery(jpqlFindAllEquipmentsConnectedWithRent, RentReturnEquipmentRecordResDto.class)
                    .setParameter("rentid", rentId)
                    .setParameter("eid", userDataDto.getId())
                    .getResultList();
                if (equipmentsList.isEmpty()) throw new EquipmentNotFoundException();
                final LocalDateTime generatedBrief = LocalDateTime.now().truncatedTo(MINUTES);
                final String returnIssuerIdentifier = rentDetails.getIssuedIdentifier().replace("WY", "ZW");
                if (rentDetails.getRentDateTime().isAfter(generatedBrief)) {
                    throw new ReturnDateBeforeRentDateException();
                }

                final LocalDateTime startTruncated = truncateToTotalHour(rentDetails.getRentDateTime());
                final LocalDateTime endTruncated = truncateToTotalHour(generatedBrief);
                final long totalRentHours = between(startTruncated, endTruncated).toHours();
                final long rentDays = totalRentHours / 24;

                final RentEntity rentEntity = session.get(RentEntity.class, rentId);
                final RentReturnEntity rentReturn = modelMapper.map(rentDetails, RentReturnEntity.class);
                rentReturn.setTotalDepositPrice(rentDetails.getTotalDepositPriceNetto());
                final Set<RentReturnEquipmentEntity> rentEquipmentEntities = new HashSet<>();
                final Set<EmailEquipmentPayloadDataDto> emailEquipmentsPayload = new HashSet<>();

                rentReturn.setEquipments(new HashSet<>());
                BigDecimal totalSumPriceNetto = new BigDecimal(0);

                for (final RentReturnEquipmentRecordResDto eqDto : equipmentsList) {
                    final BigDecimal totalPriceDays = eqDto.getPricePerDay().multiply(new BigDecimal(rentDays));
                    BigDecimal totalPriceHoursSum = eqDto.getPricePerHour();
                    if ((totalRentHours % 24) > 0) {
                        for (int i = 0; i < (totalRentHours % 24) - 1; i++) {
                            totalPriceHoursSum = totalPriceHoursSum.add(eqDto.getPriceForNextHour());
                        }
                    }
                    final BigDecimal totalPrice = totalPriceDays.add(totalPriceHoursSum);
                    final BigDecimal sumPriceNetto = totalPrice.multiply(new BigDecimal(eqDto.getCount()));

                    final BigDecimal taxValue = new BigDecimal(rentDetails.getTax());
                    final BigDecimal sumPriceBrutto = taxValue
                        .divide(new BigDecimal(100), 2, HALF_UP).multiply(sumPriceNetto).add(sumPriceNetto)
                        .setScale(2, HALF_UP);

                    final BigDecimal depositPriceBrutto = taxValue
                        .divide(new BigDecimal(100), 2, HALF_UP).multiply(eqDto.getDepositPriceNetto())
                        .add(eqDto.getDepositPriceNetto())
                        .setScale(2, HALF_UP);

                    final EquipmentEntity equipmentEntity = session
                        .getReference(EquipmentEntity.class, eqDto.getEquipmentId());
                    final RentEquipmentEntity rentEquipmentEntity = session
                        .getReference(RentEquipmentEntity.class, eqDto.getId());
                    final RentReturnEquipmentEntity returnEquipmentEntity = modelMapper
                        .map(eqDto, RentReturnEquipmentEntity.class);

                    final String jpqlIncreaseEquipmentCount =
                        "UPDATE EquipmentEntity e SET e.availableCount = e.availableCount + :rentedCount " +
                        "WHERE e.id = :eid";
                    session.createMutationQuery(jpqlIncreaseEquipmentCount)
                        .setParameter("eid", equipmentEntity.getId())
                        .setParameter("rentedCount", eqDto.getCount())
                        .executeUpdate();

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

                final String jpqlUpdateRentStatus = "UPDATE RentEntity r SET r.status = :rst WHERE r.id = :rentid";
                session.createMutationQuery(jpqlUpdateRentStatus)
                    .setParameter("rst", RETURNED).setParameter("rentid", rentId)
                    .executeUpdate();

                final String jpqlGetCustomerDetails =
                    "SELECT new pl.polsl.skirentalservice.dto.deliv_return.CustomerDetailsResDto(" +
                        "CONCAT(d.firstName, ' ', d.lastName), d.pesel, CONCAT('+', d.phoneAreaCode, ' '," +
                        "SUBSTRING(d.phoneNumber, 1, 3), ' ', SUBSTRING(d.phoneNumber, 4, 3), ' '," +
                        "SUBSTRING(d.phoneNumber, 7, 3)), d.emailAddress, CONCAT('ul. ', a.street, ' ', a.buildingNr," +
                        "IF(a.apartmentNr, CONCAT('/', a.apartmentNr), ''), ', ', a.postalCode, ' ', a.city)" +
                    ") FROM RentEntity r " +
                    "INNER JOIN r.customer c INNER JOIN c.userDetails d INNER JOIN c.locationAddress a " +
                    "WHERE r.id = :rentid";
                final CustomerDetailsResDto customerDetails = session
                    .createQuery(jpqlGetCustomerDetails, CustomerDetailsResDto.class)
                    .setParameter("rentid", rentId)
                    .getSingleResultOrNull();
                if (isNull(customerDetails)) throw new UserNotFoundException(USER);

                final var emailPayload = modelMapper.map(rentDetails, RentReturnEmailPayloadDataDto.class);
                modelMapper.map(customerDetails, emailPayload);

                final BigDecimal totalSumPriceBrutto = new BigDecimal(emailPayload.getTax())
                    .divide(new BigDecimal(100), 2, HALF_UP).multiply(totalSumPriceNetto).add(totalSumPriceNetto)
                    .setScale(2, HALF_UP);
                emailPayload.setReturnDate(generatedBrief.toString());
                emailPayload.setTotalPriceNetto(totalSumPriceNetto);
                emailPayload.setRentTime(rentDays +  " dni, " + totalRentHours + " godzin");
                emailPayload.setTotalPriceBrutto(totalSumPriceBrutto);

                final BigDecimal totalWithTax = totalSumPriceBrutto.add(rentDetails.getTotalDepositPriceBrutto());
                emailPayload.setTotalPriceWithDepositBrutto(totalWithTax);
                emailPayload.getRentEquipments().addAll(emailEquipmentsPayload);

                final String emailTopic = "SkiRent Service | Nowy zwrot: " + rentReturn.getIssuedIdentifier();
                final Map<String, Object> templateVars = new HashMap<>();
                templateVars.put("rentIdentifier", rentDetails.getIssuedIdentifier());
                templateVars.put("returnIdentifier", rentReturn.getIssuedIdentifier());
                templateVars.put("additionalDescription", isNull(description) ? "<i>Brak danych</i>" : description);
                templateVars.put("data", emailPayload);

                final MailRequestPayload customerPayload = MailRequestPayload.builder()
                    .messageResponder(emailPayload.getFullName())
                    .subject(emailTopic)
                    .templateName("create-new-return-customer.template.ftl")
                    .templateVars(templateVars)
                    .build();
                mailSocket.sendMessage(emailPayload.getEmail(), customerPayload, req);
                LOGGER.info("Successful send rent-return email message for customer. Payload: {}", customerPayload);

                final MailRequestPayload employerPayload = MailRequestPayload.builder()
                    .messageResponder(userDataDto.getFullName())
                    .subject(emailTopic)
                    .templateName("create-new-return-employer.template.ftl")
                    .templateVars(templateVars)
                    .build();
                mailSocket.sendMessage(userDataDto.getEmailAddress(), employerPayload, req);
                LOGGER.info("Successful send rent-return email message for employer. Payload: {}", employerPayload);

                final Map<String, Object> ownerTemplateVars = new HashMap<>(templateVars);
                ownerTemplateVars.put("employerFullName", userDataDto.getFullName());

                final String jpqlFindAllOwners =
                    "SELECT new pl.polsl.skirentalservice.dto.OwnerMailPayloadDto(" +
                        "CONCAT(d.firstName, ' ', d.lastName), d.emailAddress" +
                    ") FROM EmployerEntity e " +
                    "INNER JOIN e.userDetails d INNER JOIN e.role r WHERE r.alias = 'K'";
                final List<OwnerMailPayloadDto> allOwnersEmails = session
                    .createQuery(jpqlFindAllOwners, OwnerMailPayloadDto.class)
                    .getResultList();

                final MailRequestPayload ownerPayload = MailRequestPayload.builder()
                    .subject(emailTopic)
                    .templateName("create-new-return-owner.template.ftl")
                    .templateVars(ownerTemplateVars)
                    .build();
                for (final OwnerMailPayloadDto owner : allOwnersEmails) {
                    ownerPayload.setMessageResponder(owner.getFullName());
                    mailSocket.sendMessage(owner.getEmail(), ownerPayload, req);
                }
                LOGGER.info("Successful send rent-return email message for owner/owners. Payload: {}", ownerPayload);

                // TODO: generowanie pdfa

                session.persist(rentReturn);
                session.getTransaction().commit();
                alert.setType(INFO);
                alert.setMessage(
                    "Generowanie zwrotu o numerze <strong>" + returnIssuerIdentifier + "</strong> dla wypożyczenia o " +
                    "numerze <strong>" + rentDetails.getIssuedIdentifier() + "</strong> zakończone sukcesem. Potwierdzenie " +
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
