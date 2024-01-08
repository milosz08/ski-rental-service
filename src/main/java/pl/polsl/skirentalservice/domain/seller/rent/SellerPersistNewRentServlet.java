/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.domain.seller.rent;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.modelmapper.ModelMapper;
import pl.polsl.skirentalservice.core.ConfigSingleton;
import pl.polsl.skirentalservice.core.ModelMapperGenerator;
import pl.polsl.skirentalservice.core.db.HibernateDbSingleton;
import pl.polsl.skirentalservice.core.mail.Attachment;
import pl.polsl.skirentalservice.core.mail.MailRequestPayload;
import pl.polsl.skirentalservice.core.mail.MailSocketSingleton;
import pl.polsl.skirentalservice.dao.EmployerDao;
import pl.polsl.skirentalservice.dao.EquipmentDao;
import pl.polsl.skirentalservice.dao.hibernate.EmployerDaoHib;
import pl.polsl.skirentalservice.dao.hibernate.EquipmentDaoHib;
import pl.polsl.skirentalservice.dto.*;
import pl.polsl.skirentalservice.dto.customer.CustomerDetailsResDto;
import pl.polsl.skirentalservice.dto.login.LoggedUserDataDto;
import pl.polsl.skirentalservice.dto.rent.CartSingleEquipmentDataDto;
import pl.polsl.skirentalservice.dto.rent.InMemoryRentDataDto;
import pl.polsl.skirentalservice.entity.*;
import pl.polsl.skirentalservice.pdf.RentPdfDocument;
import pl.polsl.skirentalservice.pdf.dto.RentPdfDocumentDataDto;
import pl.polsl.skirentalservice.util.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static pl.polsl.skirentalservice.exception.AlreadyExistException.TooMuchEquipmentsException;
import static pl.polsl.skirentalservice.exception.NotFoundException.AnyEquipmentsInCartNotFoundException;

@Slf4j
@WebServlet("/seller/persist-new-rent")
public class SellerPersistNewRentServlet extends HttpServlet {
    private final SessionFactory sessionFactory = HibernateDbSingleton.getInstance().getSessionFactory();
    private final MailSocketSingleton mailSocket = MailSocketSingleton.getInstance();
    private final ConfigSingleton config = ConfigSingleton.getInstance();

    private final ModelMapper modelMapper = ModelMapperGenerator.getModelMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final HttpSession httpSession = req.getSession();
        final var rentData = (InMemoryRentDataDto) httpSession
            .getAttribute(SessionAttribute.INMEMORY_NEW_RENT_DATA.getName());
        if (rentData == null) {
            res.sendRedirect("/seller/customers");
            return;
        }
        final AlertTupleDto alert = new AlertTupleDto(true);
        final String loggedUser = Utils.getLoggedUserLogin(req);
        final var loggedEmployer = (LoggedUserDataDto) httpSession
            .getAttribute(SessionAttribute.LOGGED_USER_DETAILS.getName());

        try (final Session session = sessionFactory.openSession()) {
            if (rentData.getEquipments().isEmpty()) {
                throw new AnyEquipmentsInCartNotFoundException();
            }
            try {
                session.beginTransaction();

                final EmployerDao employerDao = new EmployerDaoHib(session);
                final EquipmentDao equipmentDao = new EquipmentDaoHib(session);

                final RentEntity rent = modelMapper.map(rentData, RentEntity.class);
                rent.setEquipments(new HashSet<>());

                final Set<RentEquipmentEntity> equipmentEntities = new HashSet<>();
                for (final CartSingleEquipmentDataDto cartData : rentData.getEquipments()) {
                    final Integer eqCount = equipmentDao.findAllEquipmentsInCartCount(cartData.getId());
                    if (eqCount < Integer.parseInt(cartData.getCount())) {
                        throw new TooMuchEquipmentsException();
                    }
                    final RentEquipmentEntity equipment = modelMapper.map(cartData, RentEquipmentEntity.class);
                    final EquipmentEntity refEquipment = session.get(EquipmentEntity.class, cartData.getId());
                    equipment.setId(null);
                    equipment.setTotalPrice(cartData.getPriceUnits().getTotalPriceNetto());
                    equipment.setDepositPrice(cartData.getPriceUnits().getTotalDepositPriceNetto());
                    equipment.setEquipment(refEquipment);
                    equipment.setRent(rent);
                    equipmentEntities.add(equipment);

                    equipmentDao.decreaseAvailableSelectedEquipmentCount(cartData.getId(), cartData.getCount());
                }
                rent.setId(null);
                rent.setIssuedDateTime(LocalDateTime.parse(rentData.getIssuedDateTime().replace(' ', 'T')));
                rent.setTotalPrice(rentData.getPriceUnits().getTotalPriceNetto());
                rent.setTotalDepositPrice(rentData.getPriceUnits().getTotalDepositPriceNetto());

                rent.setStatus(rentData.getParsedRentDateTime().isAfter(LocalDateTime.now()) ? RentStatus.BOOKED : RentStatus.RENTED);
                rent.setCustomer(session.get(CustomerEntity.class, rentData.getCustomerId()));
                rent.setEmployer(session.get(EmployerEntity.class, loggedEmployer.getId()));
                rent.setEquipments(equipmentEntities);

                final RentReturnEmailPayloadDataDto emailPayload = modelMapper.map(rentData, RentReturnEmailPayloadDataDto.class);
                modelMapper.map(rentData.getCustomerDetails(), emailPayload);

                final PriceUnitsDto priceUnits = rentData.getPriceUnits();
                final BigDecimal totalWithTax = priceUnits.getTotalPriceBrutto().add(priceUnits.getTotalDepositPriceBrutto());
                emailPayload.setTotalPriceWithDepositBrutto(totalWithTax);
                emailPayload.setRentTime(rentData.getDays() + " dni, " + rentData.getHours() + " godzin");

                for (final CartSingleEquipmentDataDto equipmentDataDto : rentData.getEquipments()) {
                    final var equipment = modelMapper.map(equipmentDataDto, EmailEquipmentPayloadDataDto.class);
                    emailPayload.getRentEquipments().add(equipment);
                }
                final String emailTopic = "Nowe wypożyczenie: " + rentData.getIssuedIdentifier();
                final String description = rentData.getDescription() == null
                    ? "<i>Brak danych</i>" : rentData.getDescription();

                final Map<String, Object> templateVars = new HashMap<>();
                templateVars.put("rentIdentifier", rentData.getIssuedIdentifier());
                templateVars.put("additionalDescription", description);
                templateVars.put("data", emailPayload);

                final RentPdfDocumentDataDto rentPdfDataDto = modelMapper.map(rentData, RentPdfDocumentDataDto.class);
                final CustomerDetailsResDto customerDetails = rentData.getCustomerDetails();
                rentPdfDataDto.setTotalSumPriceNetto(priceUnits.getTotalPriceNetto()
                    .add(priceUnits.getTotalDepositPriceNetto()).toString());
                rentPdfDataDto.setTotalSumPriceBrutto(priceUnits.getTotalPriceBrutto()
                    .add(priceUnits.getTotalDepositPriceBrutto()).toString());

                modelMapper.map(rentData.getCustomerDetails(), rentPdfDataDto);
                rentPdfDataDto.setAddress(customerDetails.address() + ", " + customerDetails.cityWithPostCode());
                rentPdfDataDto.setRentTime(rentData.getDays() + " dni, " + rentData.getHours() + " godzin");

                final RentPdfDocument rentPdfDocument = new RentPdfDocument(config.getUploadsDir(), rentPdfDataDto);
                rentPdfDocument.generate();

                final MailRequestPayload customerPayload = MailRequestPayload.builder()
                    .messageResponder(rentData.getCustomerDetails().fullName())
                    .subject(emailTopic)
                    .template(MailTemplate.ADD_NEW_RENT_CUSTOMER)
                    .templateVars(templateVars)
                    .attachments(List.of(new Attachment(data.filename(), data.pdfData(), pdfType)))
                    .build();
                mailSocket.sendMessage(rentData.getCustomerDetails().email(), customerPayload, req);
                log.info("Successful send rent email message for customer. Payload: {}", customerPayload);

                final MailRequestPayload employerPayload = MailRequestPayload.builder()
                    .messageResponder(loggedEmployer.getFullName())
                    .subject(emailTopic)
                    .template(MailTemplate.ADD_NEW_RENT_EMPLOYER)
                    .templateVars(templateVars)
                    .attachments(List.of(new Attachment(data.filename(), data.pdfData(), pdfType)))
                    .build();
                mailSocket.sendMessage(loggedEmployer.getEmailAddress(), employerPayload, req);
                log.info("Successful send rent email message for employer. Payload: {}", employerPayload);

                final Map<String, Object> ownerTemplateVars = new HashMap<>(templateVars);
                ownerTemplateVars.put("employerFullName", loggedEmployer.getFullName());

                final MailRequestPayload ownerPayload = MailRequestPayload.builder()
                    .subject(emailTopic)
                    .template(MailTemplate.ADD_NEW_RENT_OWNER)
                    .templateVars(ownerTemplateVars)
                    .attachments(List.of(new Attachment(data.filename(), data.pdfData(), pdfType)))
                    .build();
                for (final OwnerMailPayloadDto owner : employerDao.findAllEmployersMailSenders()) {
                    ownerPayload.setMessageResponder(owner.fullName());
                    mailSocket.sendMessage(owner.email(), ownerPayload, req);
                }
                log.info("Successful send rent email message for owner/owners. Payload: {}", ownerPayload);

                session.persist(rent);
                session.getTransaction().commit();
                alert.setType(AlertType.INFO);
                alert.setMessage(
                    "Wypożyczenie o numerze <strong>" + rentData.getIssuedIdentifier() + "</strong> zostało pomyślnie " +
                        "złożone w systemie. Szczegóły złożonego wypożyczenia znajdziesz również w wiadomości email."
                );
                httpSession.setAttribute(SessionAlert.COMMON_RENTS_PAGE_ALERT.getName(), alert);
                httpSession.removeAttribute(SessionAttribute.INMEMORY_NEW_RENT_DATA.getName());
                log.info("Successfuly persist new rent by: {} in database. Rent data: {}", loggedUser, rentData);
                res.sendRedirect("/seller/rents");
            } catch (RuntimeException ex) {
                Utils.onHibernateException(session, log, ex);
            }
        } catch (RuntimeException ex) {
            alert.setMessage(ex.getMessage());
            httpSession.setAttribute(SessionAlert.SELLER_COMPLETE_RENT_PAGE_ALERT.getName(), alert);
            log.error("Failure persist new rent by: {} in database. Rent data: {}. Cause: {}", loggedUser, rentData,
                ex.getMessage());
            res.sendRedirect("/seller/complete-rent-equipments");
        }
    }
}
