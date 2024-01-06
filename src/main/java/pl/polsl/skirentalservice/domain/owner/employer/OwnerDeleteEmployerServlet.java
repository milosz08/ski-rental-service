/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.domain.owner.employer;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.polsl.skirentalservice.core.db.HibernateDbSingleton;
import pl.polsl.skirentalservice.core.ssh.SshSocketSingleton;
import pl.polsl.skirentalservice.dao.EmployerDao;
import pl.polsl.skirentalservice.dao.hibernate.EmployerDaoHib;
import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.ssh.ExecCommandPerformer;
import pl.polsl.skirentalservice.ssh.IExecCommandPerformer;
import pl.polsl.skirentalservice.util.AlertType;
import pl.polsl.skirentalservice.util.SessionAlert;
import pl.polsl.skirentalservice.util.Utils;

import java.io.IOException;

import static pl.polsl.skirentalservice.exception.AlreadyExistException.EmployerHasOpenedRentsException;
import static pl.polsl.skirentalservice.exception.NotFoundException.UserNotFoundException;

@WebServlet("/owner/delete-employer")
public class OwnerDeleteEmployerServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(OwnerAddEmployerServlet.class);

    private final SessionFactory sessionFactory = HibernateDbSingleton.getInstance().getSessionFactory();
    private final SshSocketSingleton sshSocket = SshSocketSingleton.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final String userId = req.getParameter("id");
        final AlertTupleDto alert = new AlertTupleDto(true);

        final HttpSession httpSession = req.getSession();
        try (final Session session = sessionFactory.openSession()) {
            try {
                session.beginTransaction();
                final EmployerDao employerDao = new EmployerDaoHib(session);

                final var deletingEmployer = employerDao.findEmployerBasedId(userId)
                    .orElseThrow(() -> new UserNotFoundException(userId));
                if (employerDao.checkIfEmployerHasOpenedRents(userId)) throw new EmployerHasOpenedRentsException();

                final IExecCommandPerformer commandPerformer = new ExecCommandPerformer(sshSocket);
                commandPerformer.deleteMailbox(deletingEmployer.getEmailAddress());
                session.remove(deletingEmployer);

                alert.setType(AlertType.INFO);
                alert.setMessage(
                    "Pomyślnie usunięto pracownika z ID <strong>#" + userId + "</strong> z systemu wraz z " +
                        "jego skrzynką pocztową."
                );
                session.getTransaction().commit();
                LOGGER.info("Employer with id: {} was succesfuly removed from system.", userId);
            } catch (RuntimeException ex) {
                Utils.onHibernateException(session, LOGGER, ex);
            }
        } catch (Exception ex) {
            alert.setMessage(ex.getMessage());
            LOGGER.error("Unable to remove employer with id: {}. Cause: {}", userId, ex.getMessage());
        }
        httpSession.setAttribute(SessionAlert.OWNER_EMPLOYERS_PAGE_ALERT.getName(), alert);
        res.sendRedirect("/owner/employers");
    }
}
