/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.domain.seller.customer;

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
import pl.polsl.skirentalservice.core.ValidatorSingleton;
import pl.polsl.skirentalservice.core.db.HibernateDbSingleton;
import pl.polsl.skirentalservice.dao.CustomerDao;
import pl.polsl.skirentalservice.dao.UserDetailsDao;
import pl.polsl.skirentalservice.dao.hibernate.CustomerDaoHib;
import pl.polsl.skirentalservice.dao.hibernate.UserDetailsDaoHib;
import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.dto.customer.AddEditCustomerReqDto;
import pl.polsl.skirentalservice.dto.customer.AddEditCustomerResDto;
import pl.polsl.skirentalservice.entity.CustomerEntity;
import pl.polsl.skirentalservice.util.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Objects;

import static pl.polsl.skirentalservice.exception.AlreadyExistException.*;
import static pl.polsl.skirentalservice.exception.DateException.DateInFutureException;
import static pl.polsl.skirentalservice.exception.NotFoundException.UserNotFoundException;

@Slf4j
@WebServlet("/seller/edit-customer")
public class SellerEditCustomerServlet extends HttpServlet {
    private final SessionFactory sessionFactory = HibernateDbSingleton.getInstance().getSessionFactory();
    private final ValidatorSingleton validator = ValidatorSingleton.getInstance();
    private final ConfigSingleton config = ConfigSingleton.getInstance();

    private final ModelMapper modelMapper = ModelMapperGenerator.getModelMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final String customerId = req.getParameter("id");
        final HttpSession httpSession = req.getSession();

        final AlertTupleDto alert = Utils.getAndDestroySessionAlert(req, SessionAlert.SELLER_EDIT_CUSTOMER_PAGE_ALERT);
        var resDto = (AddEditCustomerResDto) httpSession.getAttribute(getClass().getName());

        if (Objects.isNull(resDto)) {
            try (final Session session = sessionFactory.openSession()) {
                try {
                    session.beginTransaction();
                    final CustomerDao customerDao = new CustomerDaoHib(session);

                    final var customerDetails = customerDao.findCustomerEditPageDetails(customerId)
                        .orElseThrow(() -> new UserNotFoundException(customerId));

                    resDto = new AddEditCustomerResDto(validator, customerDetails);
                    session.getTransaction().commit();
                } catch (RuntimeException ex) {
                    Utils.onHibernateException(session, log, ex);
                }
            } catch (RuntimeException ex) {
                alert.setMessage(ex.getMessage());
                httpSession.setAttribute(SessionAlert.COMMON_CUSTOMERS_PAGE_ALERT.getName(), alert);
                res.sendRedirect("/seller/customers");
            }
        }
        req.setAttribute("alertData", alert);
        req.setAttribute("addEditCustomerData", resDto);
        req.setAttribute("addEditText", "Edytuj");
        req.setAttribute("title", PageTitle.SELLER_EDIT_CUSTOMER_PAGE.getName());
        req.getRequestDispatcher("/WEB-INF/pages/seller/customer/seller-add-edit-customer.jsp").forward(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final String customerId = req.getParameter("id");
        final AlertTupleDto alert = new AlertTupleDto(true);
        final HttpSession httpSession = req.getSession();

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
                final UserDetailsDao userDetailsDao = new UserDetailsDaoHib(session);

                final CustomerEntity updatableCustomer = session.get(CustomerEntity.class, customerId);
                if (Objects.isNull(updatableCustomer)) throw new UserNotFoundException(customerId);

                if (userDetailsDao.checkIfCustomerWithSamePeselExist(reqDto.getPesel(), customerId)) {
                    throw new PeselAlreadyExistException(reqDto.getPesel(), UserRole.USER);
                }
                if (userDetailsDao.checkIfCustomerWithSamePhoneNumberExist(reqDto.getPhoneNumber(), customerId)) {
                    throw new PhoneNumberAlreadyExistException(reqDto.getPhoneNumber(), UserRole.USER);
                }
                if (userDetailsDao.checkIfCustomerWithSameEmailExist(reqDto.getEmailAddress(), customerId)) {
                    throw new EmailAddressAlreadyExistException(reqDto.getEmailAddress(), UserRole.USER);
                }

                ModelMapperGenerator.onUpdateNullableTransactTurnOn();
                modelMapper.map(reqDto, updatableCustomer.getUserDetails());
                modelMapper.map(reqDto, updatableCustomer.getLocationAddress());
                ModelMapperGenerator.onUpdateNullableTransactTurnOff();

                session.getTransaction().commit();

                alert.setType(AlertType.INFO);
                alert.setMessage("Dane klienta z ID <strong>#" + customerId + "</strong> zostały pomyślnie zaktualizowane.");
                httpSession.setAttribute(SessionAlert.COMMON_CUSTOMERS_PAGE_ALERT.getName(), alert);
                httpSession.removeAttribute(getClass().getName());
                log.info("Customer with id: {} was successfuly updated. Data: {}", customerId, reqDto);
                res.sendRedirect("/seller/customers");
            } catch (RuntimeException ex) {
                Utils.onHibernateException(session, log, ex);
            }
        } catch (RuntimeException ex) {
            alert.setMessage(ex.getMessage());
            httpSession.setAttribute(getClass().getName(), resDto);
            httpSession.setAttribute(SessionAlert.SELLER_EDIT_CUSTOMER_PAGE_ALERT.getName(), alert);
            log.error("Unable to edit existing customer with id: {}. Cause: {}", customerId, ex.getMessage());
            res.sendRedirect("/seller/edit-customer?id=" + customerId);
        }
    }
}
