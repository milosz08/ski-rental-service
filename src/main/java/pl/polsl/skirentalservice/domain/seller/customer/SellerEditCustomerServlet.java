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
import pl.polsl.skirentalservice.dao.customer.*;
import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.dao.user_details.*;
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
                    final ICustomerDao customerDao = new CustomerDao(session);

                    final var customerDetails = customerDao.findCustomerEditPageDetails(customerId).orElseThrow(() -> {
                        throw new UserNotFoundException(customerId);
                    });

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
                final IUserDetailsDao userDetailsDao = new UserDetailsDao(session);

                final CustomerEntity updatableCustomer = session.get(CustomerEntity.class, customerId);
                if (isNull(updatableCustomer)) throw new UserNotFoundException(customerId);

                if (userDetailsDao.checkIfCustomerWithSamePeselExist(reqDto.getPesel(), customerId)) {
                    throw new PeselAlreadyExistException(reqDto.getPesel(), USER);
                }
                if (userDetailsDao.checkIfCustomerWithSamePhoneNumberExist(reqDto.getPhoneNumber(), customerId)) {
                    throw new PhoneNumberAlreadyExistException(reqDto.getPhoneNumber(), USER);
                }
                if (userDetailsDao.checkIfCustomerWithSameEmailExist(reqDto.getEmailAddress(), customerId)) {
                    throw new EmailAddressAlreadyExistException(reqDto.getEmailAddress(), USER);
                }

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
