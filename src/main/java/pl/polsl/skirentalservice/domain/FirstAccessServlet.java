/*
 * Copyright (c) 2023 by multiple authors
 * Silesian University of Technology
 *
 *  File name: FirstAccessServlet.java
 *  Last modified: 21/01/2023, 16:18
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.domain;

import org.slf4j.*;
import org.hibernate.Session;

import jakarta.ejb.EJB;
import jakarta.servlet.http.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.annotation.WebServlet;

import pl.polsl.skirentalservice.ssh.*;
import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.core.ValidatorBean;
import pl.polsl.skirentalservice.dto.first_access.*;
import pl.polsl.skirentalservice.core.ssh.SshSocketBean;
import pl.polsl.skirentalservice.core.db.HibernateBean;
import pl.polsl.skirentalservice.dto.login.LoggedUserDataDto;
import pl.polsl.skirentalservice.exception.CredentialException.*;

import java.io.IOException;

import static java.util.Objects.isNull;
import static pl.polsl.skirentalservice.util.AlertType.INFO;
import static pl.polsl.skirentalservice.util.Utils.generateHash;
import static pl.polsl.skirentalservice.util.Utils.onHibernateException;
import static pl.polsl.skirentalservice.util.PageTitle.FIRST_ACCESS_PAGE;
import static pl.polsl.skirentalservice.util.SessionAlert.SELLER_DASHBOARD_PAGE_ALERT;
import static pl.polsl.skirentalservice.util.SessionAttribute.LOGGED_USER_DETAILS;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@WebServlet("/first-access")
public class FirstAccessServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(FirstAccessServlet.class);

    @EJB private HibernateBean database;
    @EJB private ValidatorBean validator;
    @EJB private SshSocketBean sshSocket;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        selfRedirect(req, res, null);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final AlertTupleDto alert = new AlertTupleDto(true);
        final FirstAccessReqDto reqDto = new FirstAccessReqDto(req);
        final FirstAccessResDto resDto = new FirstAccessResDto(validator, reqDto);

        final HttpSession httpSession = req.getSession();
        final var userDataDto = (LoggedUserDataDto) httpSession.getAttribute(LOGGED_USER_DETAILS.getName());

        try (final Session session = database.open()) {
            if (!reqDto.getPassword().equals(reqDto.getPasswordRep())) {
                throw new PasswordMismatchException("nowe hasło do konta", "powtórz nowe hasło do konta");
            }
            if (!reqDto.getEmailPassword().equals(reqDto.getEmailPasswordRep())) {
                throw new PasswordMismatchException("nowe hasło do poczty", "powtórz nowe hasło do poczty");
            }
            if (validator.someFieldsAreInvalid(reqDto)) throw new RuntimeException();
            try {
                session.beginTransaction();

                final String updateUserPassword =
                    "UPDATE EmployerEntity e SET e.password = :newPassword, e.firstAccess = false WHERE e.id = :id";
                session.createMutationQuery(updateUserPassword)
                    .setParameter("newPassword", generateHash(reqDto.getPassword()))
                    .setParameter("id", userDataDto.getId())
                    .executeUpdate();

                final IExecCommandPerformer commandPerformer = new ExecCommandPerformer(sshSocket);
                commandPerformer.updateMailboxPassword(userDataDto.getEmailAddress(), reqDto.getEmailPassword());

                alert.setType(INFO);
                alert.setMessage("Twoje hasło do konta oraz do poczty zostało pomyślnie ustawione.");
                userDataDto.setIsFirstAccess(false);
                httpSession.setAttribute(SELLER_DASHBOARD_PAGE_ALERT.getName(), alert);
                LOGGER.info("Successful changed default account password and mailbox password for user: {}", userDataDto);
                session.getTransaction().commit();
                res.sendRedirect("/seller/dashboard");
            } catch (RuntimeException ex) {
                onHibernateException(session, LOGGER, ex);
            }
        } catch (RuntimeException ex) {
            if (isNull(ex.getMessage())) alert.setActive(false);
            alert.setMessage(ex.getMessage());
            req.setAttribute("alertData", alert);
            req.setAttribute("firstAccessData", resDto);
            selfRedirect(req, res, alert);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void selfRedirect(HttpServletRequest req, HttpServletResponse res, AlertTupleDto alert)
        throws ServletException, IOException {
        req.setAttribute("title", FIRST_ACCESS_PAGE.getName());
        req.getRequestDispatcher("/WEB-INF/pages/first-access.jsp").forward(req, res);
    }
}
