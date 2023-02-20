/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 * Silesian University of Technology
 *
 *  File name: CommonProfileServlet.java
 *  Last modified: 30/01/2023, 22:11
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.domain.common;

import org.slf4j.*;
import org.hibernate.*;

import jakarta.servlet.http.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;

import pl.polsl.skirentalservice.dao.employer.*;
import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.dto.login.LoggedUserDataDto;

import java.io.IOException;

import static pl.polsl.skirentalservice.util.Utils.*;
import static pl.polsl.skirentalservice.util.SessionAlert.*;
import static pl.polsl.skirentalservice.exception.NotFoundException.*;
import static pl.polsl.skirentalservice.util.PageTitle.SELLER_PROFILE_PAGE;
import static pl.polsl.skirentalservice.core.db.HibernateUtil.getSessionFactory;
import static pl.polsl.skirentalservice.util.SessionAttribute.LOGGED_USER_DETAILS;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@WebServlet(urlPatterns = { "/seller/profile", "/owner/profile" })
public class CommonProfileServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommonProfileServlet.class);
    private final SessionFactory sessionFactory = getSessionFactory();

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final AlertTupleDto alert = getAndDestroySessionAlert(req, COMMON_PROFILE_PAGE_ALERT);
        final HttpSession httpSession = req.getSession();
        final var userDataDto = (LoggedUserDataDto) httpSession.getAttribute(LOGGED_USER_DETAILS.getName());

        try (final Session session = sessionFactory.openSession()) {
            try {
                session.beginTransaction();
                final IEmployerDao employerDao = new EmployerDao(session);

                final var employerDetails = employerDao.findEmployerPageDetails(userDataDto.getId()).orElseThrow(() -> {
                    throw new UserNotFoundException(String.valueOf(userDataDto.getId()));
                });

                session.getTransaction().commit();
                req.setAttribute("alertData", alert);
                req.setAttribute("employerData", employerDetails);
                req.setAttribute("title", SELLER_PROFILE_PAGE.getName());
                req.getRequestDispatcher("/WEB-INF/pages/" + userDataDto.getRoleEng() + "/" + userDataDto.getRoleEng() +
                    "-profile.jsp").forward(req, res);
            } catch (RuntimeException ex) {
                onHibernateException(session, LOGGER, ex);
            }
        } catch (RuntimeException ex) {
            alert.setActive(true);
            alert.setMessage(ex.getMessage());
            httpSession.setAttribute(SELLER_DASHBOARD_PAGE_ALERT.getName(), alert);
            res.sendRedirect("/" + userDataDto.getRoleEng() + "/dashboard");
        }
    }
}
