/*
 * Copyright (c) 2023 by multiple authors
 * Silesian University of Technology
 *
 *  File name: SellerAddCustomerServlet.java
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

import pl.polsl.skirentalservice.dao.*;
import pl.polsl.skirentalservice.core.*;
import pl.polsl.skirentalservice.dto.customer.*;
import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.core.ValidatorBean;
import pl.polsl.skirentalservice.core.db.HibernateBean;
import pl.polsl.skirentalservice.exception.DateException;
import pl.polsl.skirentalservice.core.mail.MailSocketBean;
import pl.polsl.skirentalservice.dto.login.LoggedUserDataDto;

import java.io.IOException;
import java.time.LocalDate;

import static pl.polsl.skirentalservice.util.UserRole.USER;
import static pl.polsl.skirentalservice.util.AlertType.INFO;
import static pl.polsl.skirentalservice.exception.AlreadyExistException.*;
import static pl.polsl.skirentalservice.util.PageTitle.SELLER_ADD_CUSTOMER_PAGE;
import static pl.polsl.skirentalservice.util.SessionAttribute.LOGGED_USER_DETAILS;
import static pl.polsl.skirentalservice.util.SessionAlert.SELLER_CUSTOMERS_PAGE_ALERT;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@WebServlet("/seller/add-customer")
public class SellerAddCustomerServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(SellerAddCustomerServlet.class);

    @EJB private HibernateBean database;
    @EJB private ValidatorBean validator;
    @EJB private ModelMapperBean modelMapper;
    @EJB private MailSocketBean mailSocket;
    @EJB private ConfigBean config;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        selfRedirect(req, res);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final AlertTupleDto alert = new AlertTupleDto(true);
        final AddEditCustomerReqDto reqDto = new AddEditCustomerReqDto(req);
        final AddEditCustomerResDto resDto = new AddEditCustomerResDto(validator, reqDto);
        if (validator.someFieldsAreInvalid(reqDto)) {
            req.setAttribute("addEditCustomerData", resDto);
            selfRedirect(req, res);
            return;
        }
        final HttpSession httpSession = req.getSession();
        try (final Session session = database.open()) {
            if (reqDto.getParsedBornDate().isAfter(LocalDate.now().minusYears(config.getCircaDateYears()))) {
                throw new DateException.DateInFutureException("data urodzenia", config.getCircaDateYears());
            }
            try {
                session.beginTransaction();

                final String jpqlFindPesel =
                    "SELECT COUNT(c.id) > 0 FROM CustomerEntity c INNER JOIN c.userDetails d WHERE d.pesel = :pesel";
                final Boolean peselExist = session.createQuery(jpqlFindPesel, Boolean.class)
                    .setParameter("pesel", reqDto.getPesel()).getSingleResult();
                if (peselExist) throw new PeselAlreadyExistException(reqDto.getPesel(), USER);

                final String jpqlFindPhoneNumber =
                    "SELECT COUNT(c.id) > 0 FROM CustomerEntity c " +
                    "INNER JOIN c.userDetails d WHERE d.phoneNumber = :phoneNumber";
                final Boolean phoneNumberExist = session.createQuery(jpqlFindPhoneNumber, Boolean.class)
                    .setParameter("phoneNumber", reqDto.getPhoneNumber()).getSingleResult();
                if (phoneNumberExist) throw new PhoneNumberAlreadyExistException(reqDto.getPhoneNumber(), USER);

                final String jpqlFindEmailAddress =
                    "SELECT COUNT(c.id) > 0 FROM CustomerEntity c " +
                    "INNER JOIN c.userDetails d WHERE d.emailAddress = :emailAddress";
                final Boolean emailAddressExist = session.createQuery(jpqlFindEmailAddress, Boolean.class)
                    .setParameter("emailAddress", reqDto.getEmailAddress()).getSingleResult();
                if (emailAddressExist) throw new EmailAddressAlreadyExistException(reqDto.getEmailAddress(), USER);

                final LocationAddressEntity locationAddress = modelMapper.map(reqDto, LocationAddressEntity.class);
                final UserDetailsEntity userDetails = modelMapper.map(reqDto, UserDetailsEntity.class);
                final CustomerEntity customer = new CustomerEntity(userDetails, locationAddress);

                session.persist(customer);
                session.getTransaction().commit();

                final var seller = (LoggedUserDataDto) httpSession.getAttribute(LOGGED_USER_DETAILS.getName());
                LOGGER.info("Successfully added new customer by: {}. Customer data: {}", seller.getLogin(), reqDto);
                alert.setType(INFO);
                alert.setMessage("Procedura dodawania nowego klienta do systemu zakończona sukcesem.");
                httpSession.setAttribute(SELLER_CUSTOMERS_PAGE_ALERT.getName(), alert);
                res.sendRedirect("/seller/customers");
            } catch (RuntimeException ex) {
                if (session.getTransaction().isActive()) {
                    LOGGER.error("Some issues appears. Transaction rollback and revert previous state...");
                    session.getTransaction().rollback();
                }
                throw ex;
            }
        } catch (RuntimeException ex) {
            alert.setMessage(ex.getMessage());
            req.setAttribute("alertData", alert);
            req.setAttribute("addEditCustomerData", resDto);
            selfRedirect(req, res);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void selfRedirect(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        req.setAttribute("addEditText", "Dodaj");
        req.setAttribute("title", SELLER_ADD_CUSTOMER_PAGE.getName());
        req.getRequestDispatcher("/WEB-INF/pages/seller/customer/seller-add-edit-customer.jsp").forward(req, res);
    }
}
