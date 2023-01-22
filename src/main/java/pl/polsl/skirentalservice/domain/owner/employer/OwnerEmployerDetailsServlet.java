/*
 * Copyright (c) 2023 by multiple authors
 * Silesian University of Technology
 *
 *  File name: OwnerEmployerDetailsServlet.java
 *  Last modified: 21/01/2023, 16:18
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.domain.owner.employer;

import org.slf4j.*;
import org.hibernate.*;

import jakarta.ejb.EJB;
import jakarta.servlet.http.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;

import java.io.IOException;

import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.core.db.HibernateBean;
import pl.polsl.skirentalservice.exception.UserNotFoundException;
import pl.polsl.skirentalservice.dto.employer.EmployerDetailsResDto;

import static java.util.Objects.isNull;
import static pl.polsl.skirentalservice.util.SessionAlert.EMPLOYERS_PAGE_ALERT;
import static pl.polsl.skirentalservice.util.PageTitle.OWNER_EMPLOYER_DETAILS_PAGE;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@WebServlet("/owner/employer-details")
public class OwnerEmployerDetailsServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(OwnerEmployerDetailsServlet.class);

    @EJB private HibernateBean database;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final String userId = req.getParameter("id");
        final AlertTupleDto alert = new AlertTupleDto(true);

        final HttpSession httpSession = req.getSession();
        try (final Session session = database.open()) {
            try {
                session.beginTransaction();

                final String jpqlFindEmployerDetails =
                    "SELECT new pl.polsl.skirentalservice.dto.employer.EmployerDetailsResDto(" +
                        "CONCAT(d.firstName, ' ', d.lastName)" +
                    ") FROM EmployerEntity e INNER JOIN e.userDetails d WHERE e.id = :uid";
                final EmployerDetailsResDto employerDetails = session
                    .createQuery(jpqlFindEmployerDetails, EmployerDetailsResDto.class)
                    .setParameter("uid", userId)
                    .getSingleResultOrNull();
                if (isNull(employerDetails)) throw new UserNotFoundException(userId);

                session.getTransaction().commit();
                req.setAttribute("employerData", employerDetails);
                req.setAttribute("title", OWNER_EMPLOYER_DETAILS_PAGE.getName());
                req.getRequestDispatcher("/WEB-INF/pages/owner/employer/owner-employer-details.jsp").forward(req, res);
            } catch (RuntimeException ex) {
                if (session.getTransaction().isActive()) {
                    LOGGER.error("Some issues appears. Transaction rollback and revert previous state...");
                    session.getTransaction().rollback();
                }
                throw ex;
            }
        } catch (RuntimeException ex) {
            alert.setMessage(ex.getMessage());
            httpSession.setAttribute(EMPLOYERS_PAGE_ALERT.getName(), alert);
            res.sendRedirect("/owner/employers");
        }
    }
}