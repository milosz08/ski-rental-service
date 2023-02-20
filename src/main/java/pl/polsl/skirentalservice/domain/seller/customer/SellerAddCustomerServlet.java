/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 * Silesian University of Technology
 *
 *  File name: SellerAddCustomerServlet.java
 *  Last modified: 08/02/2023, 22:08
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
import pl.polsl.skirentalservice.dao.user_details.*;
import pl.polsl.skirentalservice.core.ValidatorBean;
import pl.polsl.skirentalservice.core.mail.MailSocketBean;

import java.io.IOException;
import java.time.LocalDate;

import static java.util.Objects.isNull;

import static pl.polsl.skirentalservice.util.Utils.*;
import static pl.polsl.skirentalservice.util.UserRole.USER;
import static pl.polsl.skirentalservice.util.AlertType.INFO;
import static pl.polsl.skirentalservice.util.SessionAlert.*;
import static pl.polsl.skirentalservice.exception.DateException.*;
import static pl.polsl.skirentalservice.exception.AlreadyExistException.*;
import static pl.polsl.skirentalservice.core.db.HibernateUtil.getSessionFactory;
import static pl.polsl.skirentalservice.util.PageTitle.SELLER_ADD_CUSTOMER_PAGE;
import static pl.polsl.skirentalservice.core.ModelMapperGenerator.getModelMapper;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@WebServlet("/seller/add-customer")
public class SellerAddCustomerServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(SellerAddCustomerServlet.class);
    private final SessionFactory sessionFactory = getSessionFactory();
    private final ModelMapper modelMapper = getModelMapper();

    @EJB private ValidatorBean validator;
    @EJB private MailSocketBean mailSocket;
    @EJB private ConfigBean config;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final AlertTupleDto alert = getAndDestroySessionAlert(req, SELLER_ADD_CUSTOMER_PAGE_ALERT);
        var resDto = getFromSessionAndDestroy(req, getClass().getName(), AddEditCustomerResDto.class);
        if (isNull(resDto)) resDto = new AddEditCustomerResDto();

        req.setAttribute("alertData", alert);
        req.setAttribute("addEditCustomerData", resDto);
        req.setAttribute("addEditText", "Dodaj");
        req.setAttribute("title", SELLER_ADD_CUSTOMER_PAGE.getName());
        req.getRequestDispatcher("/WEB-INF/pages/seller/customer/seller-add-edit-customer.jsp").forward(req, res);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final HttpSession httpSession = req.getSession();
        final AlertTupleDto alert = new AlertTupleDto(true);
        final String loggedUser = getLoggedUserLogin(req);

        final AddEditCustomerReqDto reqDto = new AddEditCustomerReqDto(req);
        final AddEditCustomerResDto resDto = new AddEditCustomerResDto(validator, reqDto);
        if (validator.someFieldsAreInvalid(reqDto)) {
            httpSession.setAttribute(getClass().getName(), resDto);
            res.sendRedirect("/seller/add-customer");
            return;
        }
        try (final Session session = sessionFactory.openSession()) {
            if (reqDto.getParsedBornDate().isAfter(LocalDate.now().minusYears(config.getCircaDateYears()))) {
                throw new DateInFutureException("data urodzenia", config.getCircaDateYears());
            }
            try {
                session.beginTransaction();
                final IUserDetailsDao userDetailsDao = new UserDetailsDao(session);

                if (userDetailsDao.checkIfCustomerWithSamePeselExist(reqDto.getPesel(), null)) {
                    throw new PeselAlreadyExistException(reqDto.getPesel(), USER);
                }
                if (userDetailsDao.checkIfCustomerWithSamePhoneNumberExist(reqDto.getPhoneNumber(), null)) {
                    throw new PhoneNumberAlreadyExistException(reqDto.getPhoneNumber(), USER);
                }
                if (userDetailsDao.checkIfCustomerWithSameEmailExist(reqDto.getEmailAddress(), null)) {
                    throw new EmailAddressAlreadyExistException(reqDto.getEmailAddress(), USER);
                }

                final LocationAddressEntity locationAddress = modelMapper.map(reqDto, LocationAddressEntity.class);
                final UserDetailsEntity userDetails = modelMapper.map(reqDto, UserDetailsEntity.class);
                final CustomerEntity customer = new CustomerEntity(userDetails, locationAddress);

                session.persist(customer);
                session.getTransaction().commit();

                alert.setType(INFO);
                alert.setMessage("Procedura dodawania nowego klienta do systemu zakończona sukcesem.");
                httpSession.setAttribute(COMMON_CUSTOMERS_PAGE_ALERT.getName(), alert);
                httpSession.removeAttribute(getClass().getName());
                LOGGER.info("Successfully added new customer by: {}. Customer data: {}", loggedUser, reqDto);
                res.sendRedirect("/seller/customers");
            } catch (RuntimeException ex) {
                onHibernateException(session, LOGGER, ex);
            }
        } catch (RuntimeException ex) {
            alert.setMessage(ex.getMessage());
            httpSession.setAttribute(getClass().getName(), resDto);
            httpSession.setAttribute(SELLER_ADD_CUSTOMER_PAGE_ALERT.getName(), alert);
            LOGGER.error("Unable to create new customer. Cause: {}", ex.getMessage());
            res.sendRedirect("/seller/add-customer");
        }
    }
}
