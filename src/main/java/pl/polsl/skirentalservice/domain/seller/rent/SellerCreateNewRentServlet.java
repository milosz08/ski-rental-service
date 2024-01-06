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
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.polsl.skirentalservice.core.ValidatorSingleton;
import pl.polsl.skirentalservice.core.db.HibernateDbSingleton;
import pl.polsl.skirentalservice.dao.CustomerDao;
import pl.polsl.skirentalservice.dao.EmployerDao;
import pl.polsl.skirentalservice.dao.EquipmentDao;
import pl.polsl.skirentalservice.dao.RentDao;
import pl.polsl.skirentalservice.dao.hibernate.CustomerDaoHib;
import pl.polsl.skirentalservice.dao.hibernate.EmployerDaoHib;
import pl.polsl.skirentalservice.dao.hibernate.EquipmentDaoHib;
import pl.polsl.skirentalservice.dao.hibernate.RentDaoHib;
import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.dto.login.LoggedUserDataDto;
import pl.polsl.skirentalservice.dto.rent.InMemoryRentDataDto;
import pl.polsl.skirentalservice.dto.rent.NewRentDetailsReqDto;
import pl.polsl.skirentalservice.dto.rent.NewRentDetailsResDto;
import pl.polsl.skirentalservice.util.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Objects;

import static pl.polsl.skirentalservice.exception.DateException.RentDateBeforeIssuedDateException;
import static pl.polsl.skirentalservice.exception.DateException.ReturnDateBeforeRentDateException;
import static pl.polsl.skirentalservice.exception.NotFoundException.UserNotFoundException;

@WebServlet("/seller/create-new-rent")
public class SellerCreateNewRentServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(SellerCreateNewRentServlet.class);

    private final SessionFactory sessionFactory = HibernateDbSingleton.getInstance().getSessionFactory();
    private final ValidatorSingleton validator = ValidatorSingleton.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final Long customerId = NumberUtils.toLong(req.getParameter("id"));
        final HttpSession httpSession = req.getSession();

        var alert = Utils.getAndDestroySessionAlert(req, SessionAlert.SELLER_CREATE_NEW_RENT_PAGE_ALERT);
        var resDto = Utils.getFromSessionAndDestroy(req, getClass().getName(), NewRentDetailsResDto.class);

        var inMemoryRentData = (InMemoryRentDataDto) httpSession.getAttribute(SessionAttribute.INMEMORY_NEW_RENT_DATA.getName());
        var loggedUser = (LoggedUserDataDto) httpSession.getAttribute(SessionAttribute.LOGGED_USER_DETAILS.getName());

        if (!Objects.isNull(inMemoryRentData) && !inMemoryRentData.getCustomerId().equals(customerId)) {
            alert.setActive(true);
            alert.setMessage(
                "W pamięci systemu istnieje już otwarte zgłoszenie wypożyczenia dla klienta " +
                    "<a href='" + req.getContextPath() + "/seller/create-new-rent?id=" + inMemoryRentData.getCustomerId() +
                    "' class='alert-link'>" + inMemoryRentData.getCustomerFullName() + "</a>. Aby stworzyć nowe wypożyczenie, " +
                    "zamknij poprzednie lub usuń z pamięci systemu."
            );
            httpSession.setAttribute(SessionAlert.COMMON_CUSTOMERS_PAGE_ALERT.getName(), alert);
            res.sendRedirect("/seller/customers");
            return;
        }
        try (final Session session = sessionFactory.openSession()) {
            try {
                session.beginTransaction();

                final CustomerDao customerDao = new CustomerDaoHib(session);
                final EmployerDao employerDao = new EmployerDaoHib(session);
                final EquipmentDao equipmentDao = new EquipmentDaoHib(session);
                final RentDao rentDao = new RentDaoHib(session);

                final Long isSomeEquipmentsAvaialble = equipmentDao.getCountIfSomeEquipmentsAreAvailable();
                if (Objects.isNull(isSomeEquipmentsAvaialble) || isSomeEquipmentsAvaialble < 0) {
                    alert.setActive(true);
                    alert.setMessage("Aby stworzyć wypożyczenie musi być dostępny przynajmniej jeden sprzęt w " +
                        "ilości jednej sztuki na stanie.");
                    httpSession.setAttribute(SessionAlert.COMMON_CUSTOMERS_PAGE_ALERT.getName(), alert);
                    res.sendRedirect("/seller/customers");
                    return;
                }
                final var customerDetails = customerDao.findCustomerDetails(customerId)
                    .orElseThrow(() -> new UserNotFoundException(UserRole.USER));
                req.setAttribute("customerData", customerDetails);

                final var employerDetails = employerDao.findEmployerPageDetails(loggedUser.getId())
                    .orElseThrow(() -> new UserNotFoundException(UserRole.SELLER));
                req.setAttribute("employerData", employerDetails);

                if (Objects.isNull(inMemoryRentData)) {
                    inMemoryRentData = new InMemoryRentDataDto(customerId, customerDetails.fullName());
                    final LocalDateTime now = LocalDateTime.now();
                    final String issuerStaticPart = "WY/" + now.getYear() + "/" + now.getMonth().getValue() + "/";
                    final String issuerUsers = "/" + employerDetails.id() + "/" + customerId;
                    while (true) {
                        final String randomizer = RandomStringUtils.randomNumeric(4);
                        inMemoryRentData.setIssuedIdentifier(issuerStaticPart + randomizer + issuerUsers);
                        if (!rentDao.checkIfIssuerExist(randomizer)) break;
                    }
                    httpSession.setAttribute(SessionAttribute.INMEMORY_NEW_RENT_DATA.getName(), inMemoryRentData);
                }
                inMemoryRentData.setAllGood(false);
                inMemoryRentData.setCustomerDetails(customerDetails);

                if (Objects.isNull(resDto)) {
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
                Utils.onHibernateException(session, LOGGER, ex);
            }
        } catch (RuntimeException ex) {
            alert.setActive(true);
            alert.setMessage(ex.getMessage());
        }
        req.setAttribute("alertData", alert);
        req.setAttribute("rentDetails", resDto);
        req.setAttribute("title", PageTitle.SELLER_CREATE_NEW_RENT_PAGE.getName());
        req.getRequestDispatcher("/WEB-INF/pages/seller/rent/seller-create-new-rent.jsp").forward(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final String customerId = req.getParameter("id");
        final AlertTupleDto alert = new AlertTupleDto(true);
        final HttpSession httpSession = req.getSession();

        var inMemoryRentData = (InMemoryRentDataDto) httpSession.getAttribute(SessionAttribute.INMEMORY_NEW_RENT_DATA.getName());
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

            alert.setType(AlertType.INFO);
            alert.setMessage("Podstawowe ustawienia nowego wypożyczenia zostały zapisane.");
            httpSession.setAttribute(SessionAlert.SELLER_COMPLETE_RENT_PAGE_ALERT.getName(), alert);
            res.sendRedirect("/seller/complete-rent-equipments");
        } catch (RuntimeException ex) {
            alert.setMessage(ex.getMessage());
            httpSession.setAttribute(SessionAlert.SELLER_CREATE_NEW_RENT_PAGE_ALERT.getName(), alert);
            httpSession.setAttribute(getClass().getName(), resDto);
            res.sendRedirect("/seller/create-new-rent?id=" + customerId);
        }
    }
}
