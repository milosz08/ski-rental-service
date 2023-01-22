/*
 * Copyright (c) 2023 by multiple authors
 * Silesian University of Technology
 *
 *  File name: OwnerDeleteEmployerServlet.java
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

import pl.polsl.skirentalservice.ssh.*;
import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.dao.EmployerEntity;
import pl.polsl.skirentalservice.core.db.HibernateBean;
import pl.polsl.skirentalservice.core.ssh.SshSocketBean;

import static java.util.Objects.isNull;

import static pl.polsl.skirentalservice.util.AlertType.INFO;
import static pl.polsl.skirentalservice.exception.NotFoundException.*;
import static pl.polsl.skirentalservice.util.SessionAlert.OWNER_EMPLOYERS_PAGE_ALERT;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@WebServlet("/owner/delete-employer")
public class OwnerDeleteEmployerServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(OwnerAddEmployerServlet.class);

    @EJB private HibernateBean database;
    @EJB private SshSocketBean sshSocket;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final String userId = req.getParameter("id");
        final AlertTupleDto alert = new AlertTupleDto(true);

        final HttpSession httpSession = req.getSession();
        try (final Session session = database.open()) {
            try {
                session.getTransaction().begin();

                final String jpqlFindEmployer =
                    "SELECT e FROM EmployerEntity e INNER JOIN e.role r " +
                    "INNER JOIN e.userDetails d WHERE e.id = :employerId AND r.id <> 2";
                final EmployerEntity deletingEmployer = session.createQuery(jpqlFindEmployer, EmployerEntity.class)
                    .setParameter("employerId", userId)
                    .getSingleResultOrNull();
                if (isNull(deletingEmployer)) throw new UserNotFoundException(userId);

                final IExecCommandPerformer commandPerformer = new ExecCommandPerformer(sshSocket);
                commandPerformer.deleteMailbox(deletingEmployer.getEmailAddress());
                session.remove(deletingEmployer);

                // TODO: dodatkowe sprawdzanie, czy pracownik nie ma w tym czasie stworzonych przez siebie rezerwacji sprzętu

                alert.setType(INFO);
                alert.setMessage(
                    "Pomyślnie usunięto pracownika z ID <strong>#" + userId + "</strong> z systemu wraz z " +
                    "jego skrzynką pocztową."
                );
                session.getTransaction().commit();
                LOGGER.info("Employer with id: {} was succesfuly removed from system.", userId);
            } catch (RuntimeException ex) {
                if (session.getTransaction().isActive()) {
                    LOGGER.error("Some issues appears. Transaction rollback and revert previous state...");
                    session.getTransaction().rollback();
                }
                throw ex;
            }
        } catch (Exception ex) {
            alert.setMessage(ex.getMessage());
            LOGGER.error("Unable to remove employer with id: {}. Cause: {}", userId, ex.getMessage());
        }
        httpSession.setAttribute(OWNER_EMPLOYERS_PAGE_ALERT.getName(), alert);
        res.sendRedirect("/owner/employers");
    }
}
