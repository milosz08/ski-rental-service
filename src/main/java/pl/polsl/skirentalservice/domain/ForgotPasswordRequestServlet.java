/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: ForgotPasswordRequestServlet.java
 * Last modified: 6/3/23, 1:15 AM
 * Project name: ski-rental-service
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 *
 *     <http://www.apache.org/license/LICENSE-2.0>
 *
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the license.
 */

package pl.polsl.skirentalservice.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Map;
import java.util.HashMap;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.RandomStringUtils;

import pl.polsl.skirentalservice.util.*;
import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.dto.change_password.RequestToChangePasswordReqDto;
import pl.polsl.skirentalservice.dto.change_password.RequestToChangePasswordResDto;
import pl.polsl.skirentalservice.core.ValidatorSingleton;
import pl.polsl.skirentalservice.core.db.HibernateDbSingleton;
import pl.polsl.skirentalservice.core.mail.MailRequestPayload;
import pl.polsl.skirentalservice.core.mail.MailSocketSingleton;
import pl.polsl.skirentalservice.entity.EmployerEntity;
import pl.polsl.skirentalservice.entity.OtaTokenEntity;
import pl.polsl.skirentalservice.dao.employer.EmployerDao;
import pl.polsl.skirentalservice.dao.employer.IEmployerDao;

import static pl.polsl.skirentalservice.exception.NotFoundException.UserNotFoundException;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@WebServlet("/forgot-password-request")
public class ForgotPasswordRequestServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(ForgotPasswordRequestServlet.class);

    private final SessionFactory sessionFactory = HibernateDbSingleton.getInstance().getSessionFactory();
    private final ValidatorSingleton validator = ValidatorSingleton.getInstance();
    private final MailSocketSingleton mailSocket = MailSocketSingleton.getInstance();

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        req.setAttribute("alertData", Utils.getAndDestroySessionAlert(req, SessionAlert.FORGOT_PASSWORD_PAGE_ALERT));
        req.setAttribute("resetPassData", Utils.getFromSessionAndDestroy(req, getClass().getName(),
            RequestToChangePasswordResDto.class));
        req.setAttribute("title", PageTitle.FORGOT_PASSWORD_REQUEST_PAGE.getName());
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

                final var employer = employerDao.findEmployerDetails(reqDto.getLoginOrEmail())
                    .orElseThrow(() -> new UserNotFoundException(reqDto, LOGGER));

                final String token = RandomStringUtils.randomAlphanumeric(10);
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
                alert.setType(AlertType.INFO);
                alert.setMessage(
                    "Na adres email <strong>" + employer.emailAddress() + "</strong> został przesłany link aktywacyjny."
                );
                resDto.getLoginOrEmail().setValue(StringUtils.EMPTY);
                session.getTransaction().commit();
            } catch (RuntimeException ex) {
                Utils.onHibernateException(session, LOGGER, ex);
            }
        } catch (RuntimeException ex) {
            alert.setMessage(ex.getMessage());
            httpSession.setAttribute(getClass().getName(), resDto);
        }
        httpSession.setAttribute(SessionAlert.FORGOT_PASSWORD_PAGE_ALERT.getName(), alert);
        res.sendRedirect("/forgot-password-request");
    }
}
