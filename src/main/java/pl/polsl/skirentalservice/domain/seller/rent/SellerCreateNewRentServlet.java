/*
 * Copyright (c) 2023 by multiple authors
 * Silesian University of Technology
 *
 *  File name: SellerCreateNewRentServlet.java
 *  Last modified: 27/01/2023, 12:55
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.domain.seller.rent;

import org.slf4j.*;
import org.hibernate.*;

import jakarta.ejb.EJB;
import jakarta.servlet.http.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;

import pl.polsl.skirentalservice.dto.rent.*;
import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.core.ValidatorBean;
import pl.polsl.skirentalservice.dto.customer.CustomerDetailsResDto;
import pl.polsl.skirentalservice.dto.employer.EmployerDetailsResDto;

import java.io.IOException;
import java.time.LocalDateTime;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.math.NumberUtils.toLong;
import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;

import static pl.polsl.skirentalservice.util.Utils.*;
import static pl.polsl.skirentalservice.util.UserRole.*;
import static pl.polsl.skirentalservice.util.SessionAlert.*;
import static pl.polsl.skirentalservice.util.AlertType.INFO;
import static pl.polsl.skirentalservice.exception.DateException.*;
import static pl.polsl.skirentalservice.exception.NotFoundException.*;
import static pl.polsl.skirentalservice.core.db.HibernateUtil.getSessionFactory;
import static pl.polsl.skirentalservice.util.PageTitle.SELLER_CREATE_NEW_RENT_PAGE;
import static pl.polsl.skirentalservice.util.SessionAttribute.INMEMORY_NEW_RENT_DATA;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@WebServlet("/seller/create-new-rent")
public class SellerCreateNewRentServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(SellerCreateNewRentServlet.class);
    private final SessionFactory sessionFactory = getSessionFactory();

    @EJB private ValidatorBean validator;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final Long customerId = toLong(req.getParameter("id"));
        final HttpSession httpSession = req.getSession();

        var alert = getAndDestroySessionAlert(req, SELLER_CREATE_NEW_RENT_PAGE_ALERT);
        var resDto = getFromSessionAndDestroy(req, getClass().getName(), NewRentDetailsResDto.class);

        var inMemoryRentData = (InMemoryRentDataDto) httpSession.getAttribute(INMEMORY_NEW_RENT_DATA.getName());
        if (!isNull(inMemoryRentData) && !inMemoryRentData.getCustomerId().equals(customerId)) {
            alert.setActive(true);
            alert.setMessage(
                "W pamięci systemu istnieje już otwarte zgłoszenie wypożyczenia dla klienta " +
                "<a href='" + req.getContextPath() + "/seller/create-new-rent?id=" + inMemoryRentData.getCustomerId() +
                "' class='alert-link'>" + inMemoryRentData.getCustomerFullName() + "</a>. Aby stworzyć nowe wypożyczenie, " +
                "zamknij poprzednie lub usuń z pamięci systemu."
            );
            httpSession.setAttribute(COMMON_CUSTOMERS_PAGE_ALERT.getName(), alert);
            res.sendRedirect("/seller/customers");
            return;
        }
        try (final Session session = sessionFactory.openSession()) {
            try {
                session.beginTransaction();

                final String getAllCounts = "SELECT SUM(e.availableCount) > 0 FROM EquipmentEntity e";
                final Boolean isSomeEquipmentsAvaialble = session.createQuery(getAllCounts, Boolean.class)
                    .getSingleResult();
                if (!isSomeEquipmentsAvaialble) {
                    alert.setActive(true);
                    alert.setMessage("Aby stworzyć wypożyczenie musi być dostępny przynajmniej jeden sprzęt w " +
                        "ilości jednej sztuki na stanie.");
                    httpSession.setAttribute(COMMON_CUSTOMERS_PAGE_ALERT.getName(), alert);
                    res.sendRedirect("/seller/customers");
                }
                final String jpqlFindCustomerDetails =
                    "SELECT new pl.polsl.skirentalservice.dto.customer.CustomerDetailsResDto(" +
                        "c.id, CONCAT(d.firstName, ' ', d.lastName), d.emailAddress, CAST(d.bornDate AS string)," +
                        "d.pesel, CONCAT('+', d.phoneAreaCode, ' '," +
                        "SUBSTRING(d.phoneNumber, 1, 3), ' ', SUBSTRING(d.phoneNumber, 4, 3), ' '," +
                        "SUBSTRING(d.phoneNumber, 7, 3)), YEAR(NOW()) - YEAR(d.bornDate)," +
                        "d.gender, CONCAT(a.postalCode, ' ', a.city)," +
                        "CONCAT('ul. ', a.street, ' ', a.buildingNr, IF(a.apartmentNr, CONCAT('/', a.apartmentNr), ''))" +
                    ") FROM CustomerEntity c INNER JOIN c.userDetails d INNER JOIN c.locationAddress a " +
                    "WHERE c.id = :uid";
                final var customerDetails = session.createQuery(jpqlFindCustomerDetails, CustomerDetailsResDto.class)
                    .setParameter("uid", customerId)
                    .getSingleResultOrNull();
                if (isNull(customerDetails)) throw new UserNotFoundException(USER);
                req.setAttribute("customerData", customerDetails);

                final String jpqlFindEmployerDetails =
                    "SELECT new pl.polsl.skirentalservice.dto.employer.EmployerDetailsResDto(" +
                        "e.id, CONCAT(d.firstName, ' ', d.lastName), e.login, d.emailAddress, CAST(d.bornDate AS string)," +
                        "CAST(e.hiredDate AS string), d.pesel, CONCAT('+', d.phoneAreaCode, ' '," +
                        "SUBSTRING(d.phoneNumber, 1, 3), ' ', SUBSTRING(d.phoneNumber, 4, 3), ' '," +
                        "SUBSTRING(d.phoneNumber, 7, 3)), YEAR(NOW()) - YEAR(d.bornDate)," +
                        "YEAR(NOW()) - YEAR(e.hiredDate), d.gender, CONCAT(a.postalCode, ' ', a.city)," +
                        "CAST(IF(e.firstAccess, 'nieaktywowane', 'aktywowane') AS string)," +
                        "CAST(IF(e.firstAccess, 'text-danger', 'text-success') AS string)," +
                        "CONCAT('ul. ', a.street, ' ', a.buildingNr, IF(a.apartmentNr, CONCAT('/', a.apartmentNr), ''))" +
                    ") FROM EmployerEntity e INNER JOIN e.userDetails d INNER JOIN e.locationAddress a " +
                    "WHERE e.login = :login";
                final var employerDetails = session.createQuery(jpqlFindEmployerDetails, EmployerDetailsResDto.class)
                    .setParameter("login", getLoggedUserLogin(req))
                    .getSingleResultOrNull();
                if (isNull(employerDetails)) throw new UserNotFoundException(SELLER);
                req.setAttribute("employerData", employerDetails);

                if (isNull(inMemoryRentData)) {
                    inMemoryRentData = new InMemoryRentDataDto(customerId, customerDetails.getFullName());
                    final LocalDateTime now = LocalDateTime.now();
                    final String issuerStaticPart = "WY/" + now.getYear() + "/" + now.getMonth().getValue() + "/";
                    final String issuerUsers = "/" + employerDetails.getId() + "/" + customerId;
                    boolean founded = false;
                    while (true) {
                        final String randomizer = randomNumeric(4);
                        final String jpqlFindExistingIssuer =
                            "SELECT COUNT(r.id) > 0 FROM RentEntity r WHERE SUBSTRING(r.issuedIdentifier, 4, 4) = :issuer";
                        final Boolean existinIssuerExist = session.createQuery(jpqlFindExistingIssuer, Boolean.class)
                            .setParameter("issuer", randomizer)
                            .getSingleResult();
                        inMemoryRentData.setIssuedIdentifier(issuerStaticPart + randomizer + issuerUsers);
                        if (!existinIssuerExist) break;
                    }
                    httpSession.setAttribute(INMEMORY_NEW_RENT_DATA.getName(), inMemoryRentData);
                }
                inMemoryRentData.setAllGood(false);
                inMemoryRentData.setCustomerDetails(customerDetails);

                if (isNull(resDto)) {
                    resDto = new NewRentDetailsResDto();
                    resDto.getTax().setValue(inMemoryRentData.getTax());
                    resDto.getRentDateTime().setValue(inMemoryRentData.getRentDateTime());
                    resDto.getReturnDateTime().setValue(inMemoryRentData.getReturnDateTime());
                    resDto.getDescription().setValue(inMemoryRentData.getDescription());
                }
                resDto.setIssuedIdentifier(inMemoryRentData.getIssuedIdentifier());
                resDto.setIssuedDateTime(inMemoryRentData.getIssuedDateTime());
                resDto.setRentStatus(inMemoryRentData.getRentStatus());
                session.getTransaction().commit();
            } catch (RuntimeException ex) {
                onHibernateException(session, LOGGER, ex);
            }
        } catch (RuntimeException ex) {
            alert.setActive(true);
            alert.setMessage(ex.getMessage());
        }
        req.setAttribute("alertData", alert);
        req.setAttribute("rentDetails", resDto);
        req.setAttribute("title", SELLER_CREATE_NEW_RENT_PAGE.getName());
        req.getRequestDispatcher("/WEB-INF/pages/seller/rent/seller-create-new-rent.jsp").forward(req, res);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final String customerId = req.getParameter("id");
        final AlertTupleDto alert = new AlertTupleDto(true);
        final HttpSession httpSession = req.getSession();

        var inMemoryRentData = (InMemoryRentDataDto) httpSession.getAttribute(INMEMORY_NEW_RENT_DATA.getName());
        final NewRentDetailsReqDto reqDto = new NewRentDetailsReqDto(req);
        final NewRentDetailsResDto resDto = new NewRentDetailsResDto(validator, reqDto);
        try {
            if (validator.someFieldsAreInvalid(reqDto)) {
                httpSession.setAttribute(getClass().getName(), resDto);
                res.sendRedirect("/seller/create-new-rent?id=" + customerId);
                return;
            }
            resDto.setIssuedDateTime(inMemoryRentData.getIssuedDateTime());
            if (reqDto.getParsedRentDateTime().isBefore(resDto.getParsedIssuedDateTime())) {
                throw new RentDateBeforeIssuedDateException();
            }
            if (reqDto.getParsedReturnDateTime().isBefore(reqDto.getParsedRentDateTime())) {
                throw new ReturnDateBeforeRentDateException();
            }
            inMemoryRentData.setRentDateTime(reqDto.getRentDateTime().replace('T', ' '));
            inMemoryRentData.setReturnDateTime(reqDto.getReturnDateTime().replace('T', ' '));
            inMemoryRentData.setTax(reqDto.getTax());
            inMemoryRentData.setDescription(reqDto.getDescription());
            inMemoryRentData.setAllGood(true);

            resDto.setIssuedIdentifier(inMemoryRentData.getIssuedIdentifier());
            resDto.setRentStatus(inMemoryRentData.getRentStatus());

            alert.setType(INFO);
            alert.setMessage("Podstawowe ustawienia nowego wypożyczenia zostały zapisane.");
            httpSession.setAttribute(SELLER_COMPLETE_RENT_PAGE_ALERT.getName(), alert);
            res.sendRedirect("/seller/complete-rent-equipments");
        } catch (RuntimeException ex) {
            alert.setMessage(ex.getMessage());
            httpSession.setAttribute(SELLER_CREATE_NEW_RENT_PAGE_ALERT.getName(), alert);
            httpSession.setAttribute(getClass().getName(), resDto);
            res.sendRedirect("/seller/create-new-rent?id=" + customerId);
        }
    }
}
