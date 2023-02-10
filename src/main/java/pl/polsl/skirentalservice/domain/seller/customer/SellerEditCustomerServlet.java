/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 * Silesian University of Technology
 *
 *  File name: SellerEditCustomerServlet.java
 *  Last modified: 08/02/2023, 22:07
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.domain.seller.customer;

import org.slf4j.*;
import org.hibernate.*;
import org.modelmapper.ModelMapper;

import jakarta.ejb.EJB;
import jakarta.servlet.http.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;

import pl.polsl.skirentalservice.core.*;
import pl.polsl.skirentalservice.entity.*;
import pl.polsl.skirentalservice.dto.customer.*;
import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.core.ValidatorBean;

import java.io.IOException;
import java.time.LocalDate;

import static java.util.Objects.isNull;

import static pl.polsl.skirentalservice.util.Utils.*;
import static pl.polsl.skirentalservice.util.UserRole.USER;
import static pl.polsl.skirentalservice.util.AlertType.INFO;
import static pl.polsl.skirentalservice.util.SessionAlert.*;
import static pl.polsl.skirentalservice.exception.DateException.*;
import static pl.polsl.skirentalservice.core.ModelMapperGenerator.*;
import static pl.polsl.skirentalservice.exception.NotFoundException.*;
import static pl.polsl.skirentalservice.exception.AlreadyExistException.*;
import static pl.polsl.skirentalservice.core.db.HibernateUtil.getSessionFactory;
import static pl.polsl.skirentalservice.util.PageTitle.SELLER_EDIT_CUSTOMER_PAGE;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@WebServlet("/seller/edit-customer")
public class SellerEditCustomerServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(SellerEditCustomerServlet.class);
    private final SessionFactory sessionFactory = getSessionFactory();
    private final ModelMapper modelMapper = getModelMapper();

    @EJB private ValidatorBean validator;
    @EJB private ConfigBean config;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final String customerId = req.getParameter("id");
        final HttpSession httpSession = req.getSession();

        final AlertTupleDto alert = getAndDestroySessionAlert(req, SELLER_EDIT_CUSTOMER_PAGE_ALERT);
        var resDto = (AddEditCustomerResDto) httpSession.getAttribute(getClass().getName());

        if (isNull(resDto)) {
            try (final Session session = sessionFactory.openSession()) {
                try {
                    session.beginTransaction();

                    final String jpqlFindCustomerBaseId =
                        "SELECT new pl.polsl.skirentalservice.dto.customer.AddEditCustomerReqDto(" +
                            "d.firstName, d.lastName, d.pesel," +
                            "CONCAT(SUBSTRING(d.phoneNumber, 1, 3), ' ', SUBSTRING(d.phoneNumber, 4, 3), ' '," +
                            "SUBSTRING(d.phoneNumber, 7, 3))," +
                            "CAST(d.bornDate AS string), d.emailAddress, a.street," +
                            "a.buildingNr, a.apartmentNr, a.city, a.postalCode, d.gender" +
                        ") FROM CustomerEntity c " +
                        "INNER JOIN c.userDetails d INNER JOIN c.locationAddress a " +
                        "WHERE c.id = :uid";
                    final AddEditCustomerReqDto customerDetails = session
                        .createQuery(jpqlFindCustomerBaseId, AddEditCustomerReqDto.class)
                        .setParameter("uid", customerId)
                        .getSingleResultOrNull();
                    if (isNull(customerDetails)) throw new UserNotFoundException(customerId);

                    resDto = new AddEditCustomerResDto(validator, customerDetails);
                    session.getTransaction().commit();
                } catch (RuntimeException ex) {
                    if (!isNull(session)) onHibernateException(session, LOGGER, ex);
                }
            } catch (RuntimeException ex) {
                alert.setMessage(ex.getMessage());
                httpSession.setAttribute(COMMON_CUSTOMERS_PAGE_ALERT.getName(), alert);
                res.sendRedirect("/seller/customers");
            }
        }
        req.setAttribute("alertData", alert);
        req.setAttribute("addEditCustomerData", resDto);
        req.setAttribute("addEditText", "Edytuj");
        req.setAttribute("title", SELLER_EDIT_CUSTOMER_PAGE.getName());
        req.getRequestDispatcher("/WEB-INF/pages/seller/customer/seller-add-edit-customer.jsp").forward(req, res);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final String customerId = req.getParameter("id");
        final AlertTupleDto alert = new AlertTupleDto(true);
        final HttpSession httpSession = req.getSession();
        final String loggedUser = getLoggedUserLogin(req);

        final AddEditCustomerReqDto reqDto = new AddEditCustomerReqDto(req);
        final AddEditCustomerResDto resDto = new AddEditCustomerResDto(validator, reqDto);
        if (validator.someFieldsAreInvalid(reqDto)) {
            httpSession.setAttribute(getClass().getName(), resDto);
            res.sendRedirect("/seller/edit-customer?id=" + customerId);
            return;
        }
        try (final Session session = sessionFactory.openSession()) {
            if (reqDto.getParsedBornDate().isAfter(LocalDate.now().minusYears(config.getCircaDateYears()))) {
                throw new DateInFutureException("data zatrudnienia", config.getCircaDateYears());
            }
            try {
                session.beginTransaction();

                final CustomerEntity updatableCustomer = session.get(CustomerEntity.class, customerId);
                if (isNull(updatableCustomer)) throw new UserNotFoundException(customerId);

                final String jpqlFindPesel =
                    "SELECT COUNT(c.id) > 0 FROM CustomerEntity c INNER JOIN c.userDetails d " +
                    "WHERE d.pesel = :pesel AND c.id <> :uid";
                final Boolean peselExist = session.createQuery(jpqlFindPesel, Boolean.class)
                    .setParameter("pesel", reqDto.getPesel())
                    .setParameter("uid", customerId)
                    .getSingleResult();
                if (peselExist) throw new PeselAlreadyExistException(reqDto.getPesel(), USER);

                final String jpqlFindPhoneNumber =
                    "SELECT COUNT(c.id) > 0 FROM CustomerEntity c INNER JOIN c.userDetails d " +
                    "WHERE d.phoneNumber = :phoneNumber AND c.id <> :uid";
                final Boolean phoneNumberExist = session.createQuery(jpqlFindPhoneNumber, Boolean.class)
                    .setParameter("phoneNumber", reqDto.getPhoneNumber())
                    .setParameter("uid", customerId)
                    .getSingleResult();
                if (phoneNumberExist) throw new PhoneNumberAlreadyExistException(reqDto.getPhoneNumber(), USER);

                final String jpqlFindEmailAddress =
                    "SELECT COUNT(c.id) > 0 FROM CustomerEntity c INNER JOIN c.userDetails d " +
                    "WHERE d.emailAddress = :emailAddress AND c.id <> :uid";
                final Boolean emailAddressExist = session.createQuery(jpqlFindEmailAddress, Boolean.class)
                    .setParameter("emailAddress", reqDto.getEmailAddress())
                    .setParameter("uid", customerId)
                    .getSingleResult();
                if (emailAddressExist) throw new EmailAddressAlreadyExistException(reqDto.getEmailAddress(), USER);

                onUpdateNullableTransactTurnOn();
                modelMapper.map(reqDto, updatableCustomer.getUserDetails());
                modelMapper.map(reqDto, updatableCustomer.getLocationAddress());
                onUpdateNullableTransactTurnOff();

                session.getTransaction().commit();

                alert.setType(INFO);
                alert.setMessage("Dane klienta z ID <strong>#" + customerId + "</strong> zostały pomyślnie zaktualizowane.");
                httpSession.setAttribute(COMMON_CUSTOMERS_PAGE_ALERT.getName(), alert);
                httpSession.removeAttribute(getClass().getName());
                LOGGER.info("Customer with id: {} was successfuly updated. Data: {}", customerId, reqDto);
                res.sendRedirect("/seller/customers");
            } catch (RuntimeException ex) {
                onHibernateException(session, LOGGER, ex);
            }
        } catch (RuntimeException ex) {
            alert.setMessage(ex.getMessage());
            httpSession.setAttribute(getClass().getName(), resDto);
            httpSession.setAttribute(SELLER_EDIT_CUSTOMER_PAGE.getName(), alert);
            LOGGER.error("Unable to edit existing customer with id: {}. Cause: {}", customerId, ex.getMessage());
            res.sendRedirect("/seller/edit-customer?id=" + customerId);
        }
    }
}
