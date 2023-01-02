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
import at.favre.lib.crypto.bcrypt.BCrypt;

import jakarta.ejb.EJB;
import jakarta.servlet.http.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;

import java.util.Objects;
import java.io.IOException;

import pl.polsl.skirentalservice.core.*;
import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.dto.change_password.*;
import pl.polsl.skirentalservice.core.db.HibernateFactory;

import static pl.polsl.skirentalservice.util.AlertType.INFO;
import static pl.polsl.skirentalservice.util.PageTitle.CHANGE_FORGOTTEN_PASSWORD_PAGE;
import static pl.polsl.skirentalservice.util.SessionAttribute.CHANGE_PASSWORD_ALERT;

//----------------------------------------------------------------------------------------------------------------------

@WebServlet("/change-forgotten-password")
public class ChangeForgottenPasswordServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChangeForgottenPasswordServlet.class);

    @EJB private HibernateFactory database;
    @EJB private ValidatorFactory validator;

    //------------------------------------------------------------------------------------------------------------------

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final AlertTupleDto alert = new AlertTupleDto();
        selfRedirect(req, res, alert, validateTokenParameter(req, alert));
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final AlertTupleDto alert = new AlertTupleDto(true);
        final var details = validateTokenParameter(req, alert);
        if (Objects.isNull(details)) {
            selfRedirect(req, res);
            return;
        }
        final String password = req.getParameter("password");
        final String passwordRepeat = req.getParameter("passwordRepeat");

        final ChangeForgottenPasswordReqDto reqDto = new ChangeForgottenPasswordReqDto(password, passwordRepeat);
        final ChangeForgottenPasswordResDto resDto = ChangeForgottenPasswordResDto.builder()
                .password(validator.validateField(reqDto, "password", password))
                .passwordRepeat(validator.validateField(reqDto, "passwordRepeat", passwordRepeat))
                .build();
        if (!validator.allFieldsIsValid(reqDto)) {
            req.setAttribute("changePassData", resDto);
            selfRedirect(req, res, null, details);
            return;
        }
        if (!password.equals(passwordRepeat)) {
            alert.setMessage("Wartości wprowadzone w obu polach różnią się między sobą.");
            req.setAttribute("changePassData", resDto);
            selfRedirect(req, res, alert, details);
            return;
        }

        final Session session = database.open();
        final Transaction transaction = session.beginTransaction();

        session.createMutationQuery("UPDATE EmployerEntity e SET e.password = :password WHERE e.id = :employerId")
                .setParameter("password", BCrypt.withDefaults().hashToString(10, password.toCharArray()))
                .setParameter("employerId", details.getId())
                .executeUpdate();

        session.createMutationQuery("UPDATE OtaTokenEntity t SET t.isUsed = true WHERE t.id = :tokenId")
                .setParameter("tokenId", details.getTokenId())
                .executeUpdate();

        transaction.commit();
        session.close();

        alert.setMessage("Hasło do Twojego konta zostało pomyślnie zmienione.");
        alert.setType(INFO);
        LOGGER.info("Successful change password for employer account. Details: {}", details);
        final HttpSession httpSession = req.getSession();
        httpSession.setAttribute(CHANGE_PASSWORD_ALERT.getName(), alert);
        res.sendRedirect("/login");
    }

    //------------------------------------------------------------------------------------------------------------------

    private void selfRedirect(HttpServletRequest req, HttpServletResponse res, AlertTupleDto alert,
                              ChangePasswordEmployerDetailsDto employer) throws ServletException, IOException {
        req.setAttribute("alertData", alert);
        req.setAttribute("employerData", employer);
        req.setAttribute("title", CHANGE_FORGOTTEN_PASSWORD_PAGE.getName());
        req.getRequestDispatcher("/WEB-INF/pages/change-forgotten-password.jsp").forward(req, res);
    }

    //------------------------------------------------------------------------------------------------------------------

    private void selfRedirect(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        req.setAttribute("title", CHANGE_FORGOTTEN_PASSWORD_PAGE.getName());
        req.getRequestDispatcher("/WEB-INF/pages/change-forgotten-password.jsp").forward(req, res);
    }

    //------------------------------------------------------------------------------------------------------------------

    private ChangePasswordEmployerDetailsDto validateTokenParameter(HttpServletRequest req, AlertTupleDto alert) {
        final String token = req.getParameter("token");
        final Session session = database.open();

        final String jpqlFindToken =
                "SELECT new pl.polsl.skirentalservice.dto.change_password.ChangePasswordEmployerDetailsDto(" +
                    "e.id, t.id, CONCAT(d.firstName, ' ', d.lastName), e.imageUrl" +
                ") FROM OtaTokenEntity t " +
                "INNER JOIN t.employer e " +
                "INNER JOIN e.userDetails d " +
                "WHERE t.otaToken = :token AND t.isUsed = false AND t.expiredDate >= NOW()";
        final var details = session.createQuery(jpqlFindToken, ChangePasswordEmployerDetailsDto.class)
                .setParameter("token", token)
                .getSingleResultOrNull();

        if (Objects.isNull(details)) {
            alert.setMessage("Podany token nie istnieje, został już wykorzystany lub uległ przedawnieniu. Przejdź " +
                    "<a class='alert-link' href='" + req.getContextPath() + "/forgot-password-request'>tutaj</a>, " +
                    "aby wygenerować nowy token.");
            alert.setDisableContent(true);
            alert.setActive(true);
            LOGGER.warn("Attempt to change password with non-existing, expired or already used token. Token: {}", token);
        }
        session.close();
        return details;
    }
}
