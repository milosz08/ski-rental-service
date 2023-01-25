/*
 * Copyright (c) 2023 by multiple authors
 * Silesian University of Technology
 *
 *  File name: SellerEditCustomerServlet.java
 *  Last modified: 21/01/2023, 16:18
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.domain.seller.customer;

import org.slf4j.*;
import org.hibernate.Session;

import jakarta.ejb.EJB;
import jakarta.servlet.http.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;

import pl.polsl.skirentalservice.core.*;
import pl.polsl.skirentalservice.entity.*;
import pl.polsl.skirentalservice.dto.customer.*;
import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.core.ValidatorBean;
import pl.polsl.skirentalservice.core.db.HibernateBean;

import java.io.IOException;
import java.time.LocalDate;

import static java.util.Objects.isNull;

import static pl.polsl.skirentalservice.util.UserRole.USER;
import static pl.polsl.skirentalservice.util.AlertType.INFO;
import static pl.polsl.skirentalservice.exception.DateException.*;
import static pl.polsl.skirentalservice.exception.NotFoundException.*;
import static pl.polsl.skirentalservice.util.Utils.onHibernateException;
import static pl.polsl.skirentalservice.exception.AlreadyExistException.*;
import static pl.polsl.skirentalservice.util.PageTitle.SELLER_EDIT_CUSTOMER_PAGE;
import static pl.polsl.skirentalservice.util.SessionAlert.SELLER_CUSTOMERS_PAGE_ALERT;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@WebServlet("/seller/edit-customer")
public class SellerEditCustomerServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(SellerEditCustomerServlet.class);

    @EJB private HibernateBean database;
    @EJB private ValidatorBean validator;
    @EJB private ModelMapperBean modelMapper;
    @EJB private ConfigBean config;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final String userId = req.getParameter("id");
        final AlertTupleDto alert = new AlertTupleDto(true);
        final HttpSession httpSession = req.getSession();
        try (final Session session = database.open()) {
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
                    .setParameter("uid", userId)
                    .getSingleResultOrNull();
                if (isNull(customerDetails)) throw new UserNotFoundException(userId);

                session.getTransaction().commit();
                selfRedirect(req, res, new AddEditCustomerResDto(validator, customerDetails));
            } catch (RuntimeException ex) {
                onHibernateException(session, LOGGER, ex);
            }
        } catch (RuntimeException ex) {
            alert.setMessage(ex.getMessage());
            httpSession.setAttribute(SELLER_CUSTOMERS_PAGE_ALERT.getName(), alert);
            res.sendRedirect("/seller/customers");
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final String userId = req.getParameter("id");
        final AlertTupleDto alert = new AlertTupleDto(true);

        final AddEditCustomerReqDto reqDto = new AddEditCustomerReqDto(req);
        final AddEditCustomerResDto resDto = new AddEditCustomerResDto(validator, reqDto);
        if (validator.someFieldsAreInvalid(reqDto)) {
            selfRedirect(req, res, resDto);
            return;
        }
        final HttpSession httpSession = req.getSession();
        try (final Session session = database.open()) {
            if (reqDto.getParsedBornDate().isAfter(LocalDate.now().minusYears(config.getCircaDateYears()))) {
                throw new DateInFutureException("data zatrudnienia", config.getCircaDateYears());
            }
            try {
                session.beginTransaction();

                final CustomerEntity updatableCustomer = session.get(CustomerEntity.class, userId);
                if (isNull(updatableCustomer)) throw new UserNotFoundException(userId);

                final String jpqlFindPesel =
                    "SELECT COUNT(c.id) > 0 FROM CustomerEntity c INNER JOIN c.userDetails d " +
                    "WHERE d.pesel = :pesel AND c.id <> :uid";
                final Boolean peselExist = session.createQuery(jpqlFindPesel, Boolean.class)
                    .setParameter("pesel", reqDto.getPesel())
                    .setParameter("uid", userId)
                    .getSingleResult();
                if (peselExist) throw new PeselAlreadyExistException(reqDto.getPesel(), USER);

                final String jpqlFindPhoneNumber =
                    "SELECT COUNT(c.id) > 0 FROM CustomerEntity c INNER JOIN c.userDetails d " +
                    "WHERE d.phoneNumber = :phoneNumber AND c.id <> :uid";
                final Boolean phoneNumberExist = session.createQuery(jpqlFindPhoneNumber, Boolean.class)
                    .setParameter("phoneNumber", reqDto.getPhoneNumber())
                    .setParameter("uid", userId)
                    .getSingleResult();
                if (phoneNumberExist) throw new PhoneNumberAlreadyExistException(reqDto.getPhoneNumber(), USER);

                final String jpqlFindEmailAddress =
                    "SELECT COUNT(c.id) > 0 FROM CustomerEntity c INNER JOIN c.userDetails d " +
                    "WHERE d.emailAddress = :emailAddress AND c.id <> :uid";
                final Boolean emailAddressExist = session.createQuery(jpqlFindEmailAddress, Boolean.class)
                    .setParameter("emailAddress", reqDto.getEmailAddress())
                    .setParameter("uid", userId)
                    .getSingleResult();
                if (emailAddressExist) throw new EmailAddressAlreadyExistException(reqDto.getEmailAddress(), USER);

                modelMapper.onUpdateNullableTransactTurnOn();
                modelMapper.shallowCopy(reqDto, updatableCustomer.getUserDetails());
                modelMapper.shallowCopy(reqDto, updatableCustomer.getLocationAddress());
                modelMapper.onUpdateNullableTransactTurnOff();

                session.getTransaction().commit();

                alert.setType(INFO);
                httpSession.setAttribute(SELLER_CUSTOMERS_PAGE_ALERT.getName(), alert);
                LOGGER.info("Customer with id: {} was successfuly updated. Data: {}", userId, reqDto);
                alert.setMessage("Dane klienta z ID <strong>#" + userId + "</strong> zostały pomyślnie zaktualizowane.");
                res.sendRedirect("/seller/customers");
                session.getTransaction().commit();
            } catch (RuntimeException ex) {
                onHibernateException(session, LOGGER, ex);
            }
        } catch (UserNotFoundException ex) {
            alert.setMessage(ex.getMessage());
            LOGGER.error("Unable to update customer with id: {}. Cause: {}", userId, ex.getMessage());
            httpSession.setAttribute(SELLER_CUSTOMERS_PAGE_ALERT.getName(), alert);
            res.sendRedirect("/seller/customers");
        } catch (RuntimeException ex) {
            alert.setMessage(ex.getMessage());
            req.setAttribute("alertData", alert);
            selfRedirect(req, res, resDto);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void selfRedirect(HttpServletRequest req, HttpServletResponse res, AddEditCustomerResDto resDto)
        throws ServletException, IOException {
        req.setAttribute("addEditText", "Edytuj");
        req.setAttribute("addEditCustomerData", resDto);
        req.setAttribute("title", SELLER_EDIT_CUSTOMER_PAGE.getName());
        req.getRequestDispatcher("/WEB-INF/pages/seller/customer/seller-add-edit-customer.jsp").forward(req, res);
    }
}
