/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 * Silesian University of Technology
 *
 *  File name: CommonCustomerDetailsServlet.java
 *  Last modified: 30/01/2023, 18:18
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.domain.common.customer;

import org.slf4j.*;
import org.hibernate.*;

import jakarta.servlet.http.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;

import pl.polsl.skirentalservice.dao.customer.*;
import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.dto.login.LoggedUserDataDto;
import pl.polsl.skirentalservice.exception.NotFoundException;

import java.io.IOException;

import static pl.polsl.skirentalservice.util.PageTitle.*;
import static pl.polsl.skirentalservice.util.Utils.onHibernateException;
import static pl.polsl.skirentalservice.core.db.HibernateUtil.getSessionFactory;
import static pl.polsl.skirentalservice.util.SessionAttribute.LOGGED_USER_DETAILS;
import static pl.polsl.skirentalservice.util.SessionAlert.COMMON_CUSTOMERS_PAGE_ALERT;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@WebServlet(urlPatterns = { "/seller/customer-details", "/owner/customer-details" })
public class CommonCustomerDetailsServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommonCustomerDetailsServlet.class);
    private final SessionFactory sessionFactory = getSessionFactory();

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final String userId = req.getParameter("id");
        final HttpSession httpSession = req.getSession();
        final var userDataDto = (LoggedUserDataDto) httpSession.getAttribute(LOGGED_USER_DETAILS.getName());

        final AlertTupleDto alert = new AlertTupleDto(true);
        try (final Session session = sessionFactory.openSession()) {
            try {
                session.beginTransaction();
                final ICustomerDao customerDao = new CustomerDao(session);

                final var customerDetails = customerDao.findCustomerDetails(userId).orElseThrow(() -> {
                    throw new NotFoundException.UserNotFoundException(userId);
                });

                session.getTransaction().commit();
                req.setAttribute("customerData", customerDetails);
                req.setAttribute("title", COMMON_CUSTOMER_DETAILS_PAGE.getName());

                req.getRequestDispatcher("/WEB-INF/pages/" + userDataDto.getRoleEng() + "/customer/" +
                    userDataDto.getRoleEng() + "-customer-details.jsp").forward(req, res);
            } catch (RuntimeException ex) {
                onHibernateException(session, LOGGER, ex);
            }
        } catch (RuntimeException ex) {
            alert.setMessage(ex.getMessage());
            httpSession.setAttribute(COMMON_CUSTOMERS_PAGE_ALERT.getName(), alert);
            res.sendRedirect("/" + userDataDto.getRoleEng() + "/customers");
        }
    }
}
