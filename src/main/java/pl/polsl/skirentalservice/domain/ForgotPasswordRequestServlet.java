/*
 * Copyright (c) 2022 by multiple authors
 * Silesian University of Technology
 *
 *  File name: ForgotPasswordRequestServlet.java
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

import java.util.*;
import java.text.*;
import java.io.IOException;
import java.time.LocalDateTime;

import pl.polsl.skirentalservice.dto.*;
import pl.polsl.skirentalservice.dao.*;
import pl.polsl.skirentalservice.core.*;
import pl.polsl.skirentalservice.core.mail.*;
import pl.polsl.skirentalservice.dto.change_password.*;
import pl.polsl.skirentalservice.core.db.HibernateBean;
import pl.polsl.skirentalservice.exception.UserNotFoundException;

import static java.time.ZoneId.systemDefault;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static pl.polsl.skirentalservice.util.AlertType.INFO;
import static pl.polsl.skirentalservice.util.Utils.getBaseReqPath;
import static pl.polsl.skirentalservice.util.PageTitle.FORGOT_PASSWORD_REQUEST_PAGE;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@WebServlet("/forgot-password-request")
public class ForgotPasswordRequestServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(ForgotPasswordRequestServlet.class);
    private static final DateFormat DF = new SimpleDateFormat("yyyy-MM-dd, kk:mm:ss", new Locale("pl"));

    @EJB private MailSocketBean mail;
    @EJB private HibernateBean database;
    @EJB private ValidatorBean validator;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        selfRedirect(req, res, null);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final RequestToChangePasswordReqDto reqDto = new RequestToChangePasswordReqDto(req);
        final RequestToChangePasswordResDto resDto = new RequestToChangePasswordResDto(validator, reqDto);
        if (validator.someFieldsAreInvalid(reqDto)) {
            selfRedirect(req, res, resDto);
            return;
        }

        final AlertTupleDto alert = new AlertTupleDto(true);
        try (final Session session = database.open()) {
            try {
                session.beginTransaction();

                final String jpqlFindEmployer =
                    "SELECT COUNT(*) > 0 FROM EmployerEntity e " +
                    "INNER JOIN e.userDetails d " +
                    "WHERE e.login = :loginOrEmail OR d.emailAddress = :loginOrEmail";
                final boolean employerExist = session.createQuery(jpqlFindEmployer, Boolean.class)
                    .setParameter("loginOrEmail", reqDto.getLoginOrEmail())
                    .getSingleResult();
                if (!employerExist) throw new UserNotFoundException(reqDto);

                final String jpqlEmployerDetails =
                    "SELECT new pl.polsl.skirentalservice.dto.change_password.EmployerDetailsDto(" +
                        "e.id, e.login, CONCAT(d.firstName, ' ', d.lastName), d.emailAddress " +
                        ") FROM EmployerEntity e " +
                    "INNER JOIN e.userDetails d " +
                    "WHERE e.login = :loginOrEmail OR d.emailAddress = :loginOrEmail";
                final var employer = session.createQuery(jpqlEmployerDetails, EmployerDetailsDto.class)
                    .setParameter("loginOrEmail", reqDto.getLoginOrEmail())
                    .getSingleResultOrNull();

                final String token = randomAlphanumeric(10);
                final EmployerEntity employerEntity = session.getReference(EmployerEntity.class, employer.getId());
                final OtaTokenEntity otaToken = new OtaTokenEntity(token, employerEntity);
                session.persist(otaToken);

                final Map<String, String> templateVars = new HashMap<>();
                final Date expiredAt = Date.from(LocalDateTime.now().plusMinutes(10).atZone(systemDefault()).toInstant());
                templateVars.put("fullName", employer.getFullName());
                templateVars.put("token", token);
                templateVars.put("expiredToken", DF.format(expiredAt));
                templateVars.put("baseServletPath", getBaseReqPath(req));

                final MailRequestPayload payload = MailRequestPayload.builder()
                    .subject("SkiRent Service | Zmiana hasła dla użytkownika " + employer.getFullName())
                    .templateName("change-password.template.ftl")
                    .templateVars(templateVars)
                    .build();

                mail.sendMessage(employer.getEmailAddress(), payload);
                alert.setType(INFO);
                alert.setMessage(
                    "Na adres email <strong>" + employer.getEmailAddress() + "</strong> został przesłany link aktywacyjny."
                );
                resDto.getLoginOrEmail().setValue(EMPTY);
                session.getTransaction().commit();
            } catch (RuntimeException ex) {
                if (session.getTransaction().isActive()) session.getTransaction().rollback();
                throw ex;
            }
        } catch (RuntimeException ex) {
            alert.setActive(true);
            alert.setMessage(ex.getMessage());
        }
        req.setAttribute("alertData", alert);
        selfRedirect(req, res, resDto);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void selfRedirect(HttpServletRequest req, HttpServletResponse res, RequestToChangePasswordResDto resDto)
        throws ServletException, IOException {
        req.setAttribute("resetPassData", resDto);
        req.setAttribute("title", FORGOT_PASSWORD_REQUEST_PAGE.getName());
        req.getRequestDispatcher("/WEB-INF/pages/forgot-password-request.jsp").forward(req, res);
    }
}
