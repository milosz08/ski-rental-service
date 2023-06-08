/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: SellerGenerateReturnServlet.java
 * Last modified: 6/3/23, 12:06 AM
 * Project name: ski-rental-service
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 *
 *     <http://www.apache.org/license/LICENSE-2.0>
 *
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the license.
 */

package pl.polsl.skirentalservice.domain.seller.deliv_return;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import org.modelmapper.TypeToken;
import org.modelmapper.ModelMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.*;
import java.io.IOException;
import java.math.RoundingMode;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.apache.commons.lang3.StringUtils;

import pl.polsl.skirentalservice.util.*;
import pl.polsl.skirentalservice.dto.*;
import pl.polsl.skirentalservice.dto.login.LoggedUserDataDto;
import pl.polsl.skirentalservice.dto.deliv_return.CustomerDetailsReturnResDto;
import pl.polsl.skirentalservice.dto.deliv_return.RentReturnDetailsResDto;
import pl.polsl.skirentalservice.dto.deliv_return.RentReturnEquipmentRecordResDto;
import pl.polsl.skirentalservice.dto.deliv_return.ReturnAlreadyExistPayloadDto;
import pl.polsl.skirentalservice.core.ConfigSingleton;
import pl.polsl.skirentalservice.core.ModelMapperGenerator;
import pl.polsl.skirentalservice.core.db.HibernateDbSingleton;
import pl.polsl.skirentalservice.core.mail.MailSocketSingleton;
import pl.polsl.skirentalservice.core.mail.MailRequestPayload;
import pl.polsl.skirentalservice.dao.rent.RentDao;
import pl.polsl.skirentalservice.dao.rent.IRentDao;
import pl.polsl.skirentalservice.dao.customer.CustomerDao;
import pl.polsl.skirentalservice.dao.customer.ICustomerDao;
import pl.polsl.skirentalservice.dao.employer.EmployerDao;
import pl.polsl.skirentalservice.dao.employer.IEmployerDao;
import pl.polsl.skirentalservice.dao.equipment.EquipmentDao;
import pl.polsl.skirentalservice.dao.equipment.IEquipmentDao;
import pl.polsl.skirentalservice.dao.return_deliv.ReturnDao;
import pl.polsl.skirentalservice.dao.return_deliv.IReturnDao;
import pl.polsl.skirentalservice.entity.*;
import pl.polsl.skirentalservice.pdf.ReturnPdfDocument;
import pl.polsl.skirentalservice.pdf.dto.PdfEquipmentDataDto;
import pl.polsl.skirentalservice.pdf.dto.ReturnPdfDocumentDataDto;

import static pl.polsl.skirentalservice.exception.NotFoundException.UserNotFoundException;
import static pl.polsl.skirentalservice.exception.NotFoundException.RentNotFoundException;
import static pl.polsl.skirentalservice.exception.NotFoundException.EquipmentNotFoundException;
import static pl.polsl.skirentalservice.exception.DateException.ReturnDateBeforeRentDateException;
import static pl.polsl.skirentalservice.exception.AlreadyExistException.ReturnDocumentAlreadyExistException;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@WebServlet("/seller/generate-return")
public class SellerGenerateReturnServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(SellerGenerateReturnServlet.class);
    private final ModelMapper modelMapper = ModelMapperGenerator.getModelMapper();

    private final SessionFactory sessionFactory = HibernateDbSingleton.getInstance().getSessionFactory();
    private final MailSocketSingleton mailSocket = MailSocketSingleton.getInstance();
    private final ConfigSingleton config = ConfigSingleton.getInstance();

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final String rentId = req.getParameter("rentId");
        final String description = StringUtils.trimToNull(req.getParameter("description"));

        final AlertTupleDto alert = new AlertTupleDto(true);
        final HttpSession httpSession = req.getSession();
        final var userDataDto = (LoggedUserDataDto) httpSession.getAttribute(SessionAttribute.LOGGED_USER_DETAILS.getName());

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
                emailPayload.setRentTime(rentDays +  " dni, " + totalRentHours + " godzin");
                emailPayload.setTotalPriceBrutto(totalSumPriceBrutto);

                final BigDecimal totalWithTax = totalSumPriceBrutto.add(rentDetails.totalDepositPriceBrutto());
                emailPayload.setTotalPriceWithDepositBrutto(totalWithTax);
                emailPayload.getRentEquipments().addAll(emailEquipmentsPayload);

                final String emailTopic = "SkiRent Service | Nowy zwrot: " + rentReturn.getIssuedIdentifier();
                final Map<String, Object> templateVars = new HashMap<>();
                templateVars.put("rentIdentifier", rentDetails.issuedIdentifier());
                templateVars.put("returnIdentifier", rentReturn.getIssuedIdentifier());
                templateVars.put("additionalDescription", Objects.isNull(description) ? "<i>Brak danych</i>" : description);
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
                alert.setType(AlertType.INFO);
                alert.setMessage(
                    "Generowanie zwrotu o numerze <strong>" + returnIssuerIdentifier + "</strong> dla wypożyczenia o " +
                    "numerze <strong>" + rentDetails.issuedIdentifier() + "</strong> zakończone sukcesem. Potwierdzenie " +
                    "wraz z podsumowaniem zostało wysłane również na adres email."
                );
                httpSession.setAttribute(SessionAlert.COMMON_RETURNS_PAGE_ALERT.getName(), alert);
                res.sendRedirect("/seller/returns");
            } catch (RuntimeException ex) {
                Utils.onHibernateException(session, LOGGER, ex);
            }
        } catch (RuntimeException ex) {
            alert.setMessage(ex.getMessage());
            httpSession.setAttribute(SessionAlert.COMMON_RENTS_PAGE_ALERT.getName(), alert);
            res.sendRedirect("/seller/rents");
        }
    }
}
