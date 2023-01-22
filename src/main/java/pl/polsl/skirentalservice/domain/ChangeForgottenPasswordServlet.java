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
import pl.polsl.skirentalservice.core.db.HibernateBean;
import pl.polsl.skirentalservice.exception.CredentialException.*;

import static java.util.Objects.isNull;
import static pl.polsl.skirentalservice.util.AlertType.INFO;
import static pl.polsl.skirentalservice.util.Utils.generateHash;
import static pl.polsl.skirentalservice.util.SessionAlert.LOGIN_PAGE_ALERT;
import static pl.polsl.skirentalservice.util.PageTitle.CHANGE_FORGOTTEN_PASSWORD_PAGE;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@WebServlet("/change-forgotten-password")
public class ChangeForgottenPasswordServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChangeForgottenPasswordServlet.class);

    @EJB private HibernateBean database;
    @EJB private ValidatorBean validator;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final AlertTupleDto alert = new AlertTupleDto();
        ChangePasswordEmployerDetailsDto dto = null;
        try (final Session session = database.open()) {
            dto = validateTokenParameter(session, req);
        } catch (Exception ex) {
            alert.setActive(true);
            alert.setMessage(ex.getMessage());
            alert.setDisableContent(true);
        }
        selfRedirect(req, res, alert, dto);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final AlertTupleDto alert = new AlertTupleDto(true);
        final HttpSession httpSession = req.getSession();

        final ChangeForgottenPasswordReqDto reqDto = new ChangeForgottenPasswordReqDto(req);
        final ChangeForgottenPasswordResDto resDto = new ChangeForgottenPasswordResDto(validator, reqDto);
        final boolean isPasswordsDiff = !reqDto.getPassword().equals(reqDto.getPasswordRepeat());
        if (validator.someFieldsAreInvalid(reqDto) || isPasswordsDiff) {
            AlertTupleDto alertTupleDto = null;
            if (isPasswordsDiff) {
                alert.setMessage("Wartości wprowadzone w obu polach różnią się między sobą.");
                alertTupleDto = alert;
            }
            req.setAttribute("changePassData", resDto);
            selfRedirect(req, res, alertTupleDto, null);
            return;
        }
        try (final Session session = database.open()) {
            try {
                session.beginTransaction();

                final var details = validateTokenParameter(session, req);
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
                res.sendRedirect("/login");
            } catch (RuntimeException ex) {
                if (session.getTransaction().isActive()) {
                    LOGGER.error("Some issues appears. Transaction rollback and revert previous state...");
                    session.getTransaction().rollback();
                }
                throw ex;
            }
        } catch (RuntimeException ex) {
            alert.setDisableContent(true);
            alert.setMessage(ex.getMessage());
            req.setAttribute("title", CHANGE_FORGOTTEN_PASSWORD_PAGE.getName());
            req.getRequestDispatcher("/WEB-INF/pages/change-forgotten-password.jsp").forward(req, res);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void selfRedirect(HttpServletRequest req, HttpServletResponse res, AlertTupleDto alert,
                              ChangePasswordEmployerDetailsDto employer) throws ServletException, IOException {
        req.setAttribute("alertData", alert);
        req.setAttribute("employerData", employer);
        req.setAttribute("title", CHANGE_FORGOTTEN_PASSWORD_PAGE.getName());
        req.getRequestDispatcher("/WEB-INF/pages/change-forgotten-password.jsp").forward(req, res);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private ChangePasswordEmployerDetailsDto validateTokenParameter(Session session, HttpServletRequest req) {
        final String token = req.getParameter("token");
        final String jpqlFindToken =
            "SELECT new pl.polsl.skirentalservice.dto.change_password.ChangePasswordEmployerDetailsDto(" +
                "e.id, t.id, CONCAT(d.firstName, ' ', d.lastName)," +
                "IFNULL(e.imageUrl, 'static/images/default-profile-image.svg')" +
            ") FROM OtaTokenEntity t " +
            "INNER JOIN t.employer e " +
            "INNER JOIN e.userDetails d " +
            "WHERE t.otaToken = :token AND t.isUsed = false AND t.expiredDate >= NOW()";
        final var details = session.createQuery(jpqlFindToken, ChangePasswordEmployerDetailsDto.class)
                .setParameter("token", token)
                .getSingleResultOrNull();
        if (isNull(details)) throw new OtaTokenNotFoundException(req, token, LOGGER);
        return details;
    }
}
