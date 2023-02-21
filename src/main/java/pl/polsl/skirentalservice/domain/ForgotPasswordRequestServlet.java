/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 * Silesian University of Technology
 *
 *  File name: ForgotPasswordRequestServlet.java
 *  Last modified: 30/01/2023, 18:16
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

import pl.polsl.skirentalservice.dto.*;
import pl.polsl.skirentalservice.core.*;
import pl.polsl.skirentalservice.entity.*;
import pl.polsl.skirentalservice.core.mail.*;
import pl.polsl.skirentalservice.dao.employer.*;
import pl.polsl.skirentalservice.dto.change_password.*;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;

import static pl.polsl.skirentalservice.util.Utils.*;
import static pl.polsl.skirentalservice.util.AlertType.INFO;
import static pl.polsl.skirentalservice.exception.NotFoundException.*;
import static pl.polsl.skirentalservice.core.db.HibernateUtil.getSessionFactory;
import static pl.polsl.skirentalservice.util.PageTitle.FORGOT_PASSWORD_REQUEST_PAGE;
import static pl.polsl.skirentalservice.util.SessionAlert.FORGOT_PASSWORD_PAGE_ALERT;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@WebServlet("/forgot-password-request")
public class ForgotPasswordRequestServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(ForgotPasswordRequestServlet.class);
    private static final DateFormat DF = new SimpleDateFormat("yyyy-MM-dd, kk:mm:ss", new Locale("pl"));
    private final SessionFactory sessionFactory = getSessionFactory();

    @EJB private ValidatorBean validator;
    @EJB private MailSocketBean mailSocket;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        req.setAttribute("alertData", getAndDestroySessionAlert(req, FORGOT_PASSWORD_PAGE_ALERT));
        req.setAttribute("resetPassData", getFromSessionAndDestroy(req, getClass().getName(),
            RequestToChangePasswordResDto.class));
        req.setAttribute("title", FORGOT_PASSWORD_REQUEST_PAGE.getName());
        req.getRequestDispatcher("/WEB-INF/pages/forgot-password-request.jsp").forward(req, res);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final HttpSession httpSession = req.getSession();
        final RequestToChangePasswordReqDto reqDto = new RequestToChangePasswordReqDto(req);
        final RequestToChangePasswordResDto resDto = new RequestToChangePasswordResDto(validator, reqDto);
        if (validator.someFieldsAreInvalid(reqDto)) {
            httpSession.setAttribute(getClass().getName(), resDto);
            res.sendRedirect("/forgot-password-request");
            return;
        }
        final AlertTupleDto alert = new AlertTupleDto(true);
        try (final Session session = sessionFactory.openSession()) {
            try {
                session.beginTransaction();
                final IEmployerDao employerDao = new EmployerDao(session);

                final var employer = employerDao.findEmployerDetails(reqDto.getLoginOrEmail()).orElseThrow(() -> {
                    throw new UserNotFoundException(reqDto, LOGGER);
                });

                final String token = randomAlphanumeric(10);
                final EmployerEntity employerEntity = session.getReference(EmployerEntity.class, employer.id());
                final OtaTokenEntity otaToken = new OtaTokenEntity(token, employerEntity);
                session.persist(otaToken);

                final Map<String, Object> templateVars = new HashMap<>();
                templateVars.put("token", token);

                final MailRequestPayload payload = MailRequestPayload.builder()
                    .messageResponder(employer.fullName())
                    .subject("SkiRent Service | Zmiana hasła dla użytkownika " + employer.fullName())
                    .templateName("change-password.template.ftl")
                    .templateVars(templateVars)
                    .build();

                mailSocket.sendMessage(employer.emailAddress(), payload, req);
                alert.setType(INFO);
                alert.setMessage(
                    "Na adres email <strong>" + employer.emailAddress() + "</strong> został przesłany link aktywacyjny."
                );
                resDto.getLoginOrEmail().setValue(EMPTY);
                session.getTransaction().commit();
            } catch (RuntimeException ex) {
                onHibernateException(session, LOGGER, ex);
            }
        } catch (RuntimeException ex) {
            alert.setMessage(ex.getMessage());
            httpSession.setAttribute(getClass().getName(), resDto);
        }
        httpSession.setAttribute(FORGOT_PASSWORD_PAGE_ALERT.getName(), alert);
        res.sendRedirect("/forgot-password-request");
    }
}
