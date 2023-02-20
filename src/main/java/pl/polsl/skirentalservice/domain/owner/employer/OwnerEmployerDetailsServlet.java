/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 * Silesian University of Technology
 *
 *  File name: OwnerEmployerDetailsServlet.java
 *  Last modified: 30/01/2023, 18:13
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.domain.owner.employer;

import org.slf4j.*;
import org.hibernate.*;

import jakarta.servlet.http.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;

import java.io.IOException;

import pl.polsl.skirentalservice.dao.employer.*;
import pl.polsl.skirentalservice.dto.AlertTupleDto;

import static pl.polsl.skirentalservice.exception.NotFoundException.*;
import static pl.polsl.skirentalservice.util.Utils.onHibernateException;
import static pl.polsl.skirentalservice.core.db.HibernateUtil.getSessionFactory;
import static pl.polsl.skirentalservice.util.SessionAlert.OWNER_EMPLOYERS_PAGE_ALERT;
import static pl.polsl.skirentalservice.util.PageTitle.OWNER_EMPLOYER_DETAILS_PAGE;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@WebServlet("/owner/employer-details")
public class OwnerEmployerDetailsServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(OwnerEmployerDetailsServlet.class);
    private final SessionFactory sessionFactory = getSessionFactory();

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final String userId = req.getParameter("id");
        final AlertTupleDto alert = new AlertTupleDto(true);

        final HttpSession httpSession = req.getSession();
        try (final Session session = sessionFactory.openSession()) {
            try {
                session.beginTransaction();
                final IEmployerDao employerDao = new EmployerDao(session);

                final var employerDetails = employerDao.findEmployerPageDetails(userId).orElseThrow(() -> {
                    throw new UserNotFoundException(userId);
                });

                session.getTransaction().commit();
                req.setAttribute("employerData", employerDetails);
                req.setAttribute("title", OWNER_EMPLOYER_DETAILS_PAGE.getName());
                req.getRequestDispatcher("/WEB-INF/pages/owner/employer/owner-employer-details.jsp").forward(req, res);
            } catch (RuntimeException ex) {
                onHibernateException(session, LOGGER, ex);
            }
        } catch (RuntimeException ex) {
            alert.setMessage(ex.getMessage());
            httpSession.setAttribute(OWNER_EMPLOYERS_PAGE_ALERT.getName(), alert);
            res.sendRedirect("/owner/employers");
        }
    }
}
