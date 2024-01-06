/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.domain.common.customer;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import pl.polsl.skirentalservice.core.db.HibernateDbSingleton;
import pl.polsl.skirentalservice.dao.CustomerDao;
import pl.polsl.skirentalservice.dao.hibernate.CustomerDaoHib;
import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.dto.login.LoggedUserDataDto;
import pl.polsl.skirentalservice.util.PageTitle;
import pl.polsl.skirentalservice.util.SessionAlert;
import pl.polsl.skirentalservice.util.SessionAttribute;
import pl.polsl.skirentalservice.util.Utils;

import java.io.IOException;

import static pl.polsl.skirentalservice.exception.NotFoundException.UserNotFoundException;

@Slf4j
@WebServlet(urlPatterns = { "/seller/customer-details", "/owner/customer-details" })
public class CommonCustomerDetailsServlet extends HttpServlet {
    private final SessionFactory sessionFactory = HibernateDbSingleton.getInstance().getSessionFactory();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final String userId = req.getParameter("id");
        final HttpSession httpSession = req.getSession();
        final var userDataDto = (LoggedUserDataDto) httpSession.getAttribute(SessionAttribute.LOGGED_USER_DETAILS.getName());

        final AlertTupleDto alert = new AlertTupleDto(true);
        try (final Session session = sessionFactory.openSession()) {
            try {
                session.beginTransaction();
                final CustomerDao customerDao = new CustomerDaoHib(session);

                final var customerDetails = customerDao.findCustomerDetails(userId)
                    .orElseThrow(() -> new UserNotFoundException(userId));

                session.getTransaction().commit();
                req.setAttribute("customerData", customerDetails);
                req.setAttribute("title", PageTitle.COMMON_CUSTOMER_DETAILS_PAGE.getName());

                req.getRequestDispatcher("/WEB-INF/pages/" + userDataDto.getRoleEng() + "/customer/" +
                    userDataDto.getRoleEng() + "-customer-details.jsp").forward(req, res);
            } catch (RuntimeException ex) {
                Utils.onHibernateException(session, log, ex);
            }
        } catch (RuntimeException ex) {
            alert.setMessage(ex.getMessage());
            httpSession.setAttribute(SessionAlert.COMMON_CUSTOMERS_PAGE_ALERT.getName(), alert);
            res.sendRedirect("/" + userDataDto.getRoleEng() + "/customers");
        }
    }
}
