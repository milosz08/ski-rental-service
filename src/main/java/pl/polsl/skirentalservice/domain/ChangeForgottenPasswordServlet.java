/*
 * Copyright (c) 2022 by multiple authors
 * Silesian University of Technology
 *
 *  File name: ChangeForgottenPasswordServlet.java
 *  Last modified: 31/12/2022, 17:16
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.domain;

import org.slf4j.*;
import org.hibernate.*;

import jakarta.ejb.EJB;
import jakarta.servlet.http.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;

import java.io.IOException;

import pl.polsl.skirentalservice.core.*;
import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.dto.change_password.*;
import pl.polsl.skirentalservice.exception.CredentialException.*;

import static java.util.Objects.isNull;

import static pl.polsl.skirentalservice.util.Utils.*;
import static pl.polsl.skirentalservice.util.SessionAlert.*;
import static pl.polsl.skirentalservice.util.AlertType.INFO;
import static pl.polsl.skirentalservice.core.db.HibernateUtil.getSessionFactory;
import static pl.polsl.skirentalservice.util.PageTitle.CHANGE_FORGOTTEN_PASSWORD_PAGE;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@WebServlet("/change-forgotten-password")
public class ChangeForgottenPasswordServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChangeForgottenPasswordServlet.class);
    private final SessionFactory sessionFactory = getSessionFactory();

    @EJB private ValidatorBean validator;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        AlertTupleDto alert = getAndDestroySessionAlert(req, CHANGE_FORGOTTEN_PASSWORD_PAGE_ALERT);
        if (isNull(alert)) alert = new AlertTupleDto();
        final String token = req.getParameter("token");

        try (final Session session = sessionFactory.openSession()) {
            try {
                session.beginTransaction();

                final String jpqlFindToken =
                    "SELECT new pl.polsl.skirentalservice.dto.change_password.ChangePasswordEmployerDetailsDto(" +
                        "e.id, t.id, CONCAT(d.firstName, ' ', d.lastName)," +
                        "CASE WHEN e.imageUrl IS NULL THEN 'static/images/default-profile-image.svg' ELSE e.imageUrl END" +
                        ") FROM OtaTokenEntity t " +
                        "INNER JOIN t.employer e " +
                        "INNER JOIN e.userDetails d " +
                        "WHERE t.otaToken = :token AND t.isUsed = false AND t.expiredDate >= NOW()";
                final var details = session.createQuery(jpqlFindToken, ChangePasswordEmployerDetailsDto.class)
                    .setParameter("token", token)
                    .getSingleResultOrNull();
                if (isNull(details)) throw new OtaTokenNotFoundException(req, token, LOGGER);

                req.setAttribute("employerData", details);
                session.getTransaction().commit();
            } catch (RuntimeException ex) {
                onHibernateException(session, LOGGER, ex);
            }
        } catch (RuntimeException ex) {
            alert.setActive(true);
            alert.setDisableContent(true);
            alert.setMessage(ex.getMessage());
        }
        req.setAttribute("changePassData", getFromSessionAndDestroy(req, getClass().getName(),
            ChangeForgottenPasswordResDto.class));
        req.setAttribute("alertData", alert);
        req.setAttribute("title", CHANGE_FORGOTTEN_PASSWORD_PAGE.getName());
        req.getRequestDispatcher("/WEB-INF/pages/change-forgotten-password.jsp").forward(req, res);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final String token = req.getParameter("token");
        final AlertTupleDto alert = new AlertTupleDto(true);
        final HttpSession httpSession = req.getSession();

        final ChangeForgottenPasswordReqDto reqDto = new ChangeForgottenPasswordReqDto(req);
        final ChangeForgottenPasswordResDto resDto = new ChangeForgottenPasswordResDto(validator, reqDto);
        if (validator.someFieldsAreInvalid(reqDto)) {
            httpSession.setAttribute(getClass().getName(), resDto);
            res.sendRedirect("/change-forgotten-password?token=" + token);
            return;
        }
        try (final Session session = sessionFactory.openSession()) {
            if (!reqDto.getPassword().equals(reqDto.getPasswordRepeat())) {
                throw new PasswordMismatchException("hasło", "powtórz hasło");
            }
            try {
                session.beginTransaction();

                final String jpqlFindToken =
                    "SELECT new pl.polsl.skirentalservice.dto.change_password.TokenDetailsDto(e.id, t.id) " +
                    "FROM OtaTokenEntity t INNER JOIN t.employer e " +
                    "WHERE t.otaToken = :token AND t.isUsed = false AND t.expiredDate >= NOW()";
                final var details = session.createQuery(jpqlFindToken, TokenDetailsDto.class)
                    .setParameter("token", token)
                    .getSingleResultOrNull();
                if (isNull(details)) throw new OtaTokenNotFoundException(req, token, LOGGER);

                session.createMutationQuery("UPDATE EmployerEntity e SET e.password = :password WHERE e.id = :employerId")
                    .setParameter("password", generateHash(reqDto.getPassword()))
                    .setParameter("employerId", details.getId())
                    .executeUpdate();
                session.createMutationQuery("UPDATE OtaTokenEntity t SET t.isUsed = true WHERE t.id = :tokenId")
                    .setParameter("tokenId", details.getTokenId())
                    .executeUpdate();

                session.getTransaction().commit();
                alert.setMessage("Hasło do Twojego konta zostało pomyślnie zmienione.");
                alert.setType(INFO);
                LOGGER.info("Successful change password for employer account. Details: {}", details);
                httpSession.setAttribute(LOGIN_PAGE_ALERT.getName(), alert);
                httpSession.removeAttribute(getClass().getName());
                res.sendRedirect("/login");
            } catch (RuntimeException ex) {
                onHibernateException(session, LOGGER, ex);
            }
        } catch (RuntimeException ex) {
            if (!(ex instanceof PasswordMismatchException)) alert.setDisableContent(true);
            alert.setMessage(ex.getMessage());
            httpSession.setAttribute(getClass().getName(), resDto);
            httpSession.setAttribute(CHANGE_FORGOTTEN_PASSWORD_PAGE_ALERT.getName(), alert);
            res.sendRedirect("/change-forgotten-password?token=" + token);
        }
    }
}
