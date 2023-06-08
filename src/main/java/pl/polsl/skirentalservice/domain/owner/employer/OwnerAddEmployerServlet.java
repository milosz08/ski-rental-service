/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: OwnerAddEmployerServlet.java
 * Last modified: 6/2/23, 11:53 PM
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

package pl.polsl.skirentalservice.domain.owner.employer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import org.modelmapper.ModelMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Map;
import java.util.Locale;
import java.util.Objects;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.RandomStringUtils;

import pl.polsl.skirentalservice.util.*;
import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.dto.login.LoggedUserDataDto;
import pl.polsl.skirentalservice.dto.employer.AddEditEmployerReqDto;
import pl.polsl.skirentalservice.dto.employer.AddEditEmployerResDto;
import pl.polsl.skirentalservice.dto.employer.AddEmployerMailPayload;
import pl.polsl.skirentalservice.core.ConfigSingleton;
import pl.polsl.skirentalservice.core.ModelMapperGenerator;
import pl.polsl.skirentalservice.core.ValidatorSingleton;
import pl.polsl.skirentalservice.core.db.HibernateDbSingleton;
import pl.polsl.skirentalservice.core.mail.MailSocketSingleton;
import pl.polsl.skirentalservice.core.mail.MailRequestPayload;
import pl.polsl.skirentalservice.core.ssh.SshSocketSingleton;
import pl.polsl.skirentalservice.dao.employer.EmployerDao;
import pl.polsl.skirentalservice.dao.employer.IEmployerDao;
import pl.polsl.skirentalservice.dao.user_details.UserDetailsDao;
import pl.polsl.skirentalservice.dao.user_details.IUserDetailsDao;
import pl.polsl.skirentalservice.entity.RoleEntity;
import pl.polsl.skirentalservice.entity.EmployerEntity;
import pl.polsl.skirentalservice.entity.UserDetailsEntity;
import pl.polsl.skirentalservice.entity.LocationAddressEntity;
import pl.polsl.skirentalservice.ssh.ExecCommandPerformer;
import pl.polsl.skirentalservice.ssh.IExecCommandPerformer;

import static pl.polsl.skirentalservice.exception.AlreadyExistException.PeselAlreadyExistException;
import static pl.polsl.skirentalservice.exception.AlreadyExistException.PhoneNumberAlreadyExistException;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@WebServlet("/owner/add-employer")
public class OwnerAddEmployerServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(OwnerAddEmployerServlet.class);
    private final ModelMapper modelMapper = ModelMapperGenerator.getModelMapper();

    private final SessionFactory sessionFactory = HibernateDbSingleton.getInstance().getSessionFactory();
    private final ValidatorSingleton validator = ValidatorSingleton.getInstance();
    private final MailSocketSingleton mailSocket = MailSocketSingleton.getInstance();
    private final SshSocketSingleton sshSocket = SshSocketSingleton.getInstance();
    private final ConfigSingleton config = ConfigSingleton.getInstance();

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final AlertTupleDto alert = Utils.getAndDestroySessionAlert(req, SessionAlert.OWNER_ADD_EMPLOYER_PAGE_ALERT);
        var resDto = Utils.getFromSessionAndDestroy(req, getClass().getName(), AddEditEmployerResDto.class);
        if (Objects.isNull(resDto)) resDto = new AddEditEmployerResDto();
        req.setAttribute("alertData", alert);
        req.setAttribute("addEditEmployerData", resDto);
        req.setAttribute("addEditText", "Dodaj");
        req.setAttribute("title", PageTitle.OWNER_ADD_EMPLOYER_PAGE.getName());
        req.getRequestDispatcher("/WEB-INF/pages/owner/employer/owner-add-edit-employer.jsp").forward(req, res);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        final HttpSession httpSession = req.getSession();
        final AlertTupleDto alert = new AlertTupleDto(true);

        final AddEditEmployerReqDto reqDto = new AddEditEmployerReqDto(req);
        final AddEditEmployerResDto resDto = new AddEditEmployerResDto(validator, reqDto);
        if (validator.someFieldsAreInvalid(reqDto)) {
            httpSession.setAttribute(getClass().getName(), resDto);
            res.sendRedirect("/owner/add-employer");
            return;
        }

        final IExecCommandPerformer commandPerformer = new ExecCommandPerformer(sshSocket);
        try (final Session session = sessionFactory.openSession()) {
            reqDto.validateDates(config);
            String email = "";
            try {
                session.beginTransaction();

                final IEmployerDao employerDao = new EmployerDao(session);
                final IUserDetailsDao userDetailsDao = new UserDetailsDao(session);

                if (userDetailsDao.checkIfEmployerWithSamePeselExist(reqDto.getPesel(), null)) {
                    throw new PeselAlreadyExistException(reqDto.getPesel(), UserRole.SELLER);
                }
                if (userDetailsDao.checkIfEmployerWithSamePhoneNumberExist(reqDto.getPhoneNumber(), null)) {
                    throw new PhoneNumberAlreadyExistException(reqDto.getPhoneNumber(), UserRole.SELLER);
                }
                final RoleEntity role = session.get(RoleEntity.class, 1);
                if (Objects.isNull(role)) throw new RuntimeException("Podana rola nie istnieje w systemie.");

                String login;
                boolean emailExist;
                do {
                    final String withoutAccents = StringUtils.stripAccents(reqDto.getFirstName().substring(0, 3) +
                        reqDto.getLastName().substring(0, 3));
                    login = withoutAccents.toLowerCase(Locale.ENGLISH) + RandomStringUtils.randomNumeric(3);
                    emailExist = employerDao.checkIfLoginAlreadyExist(login);
                } while (emailExist);

                email = login + mailSocket.getDomain();
                final String mailPassword = RandomStringUtils.randomAlphanumeric(10);
                final String passwordRaw = RandomStringUtils.randomAlphanumeric(10);
                final String passowordDecoded = Utils.generateHash(passwordRaw);

                final UserDetailsEntity userDetails = modelMapper.map(reqDto, UserDetailsEntity.class);
                userDetails.setBornDate(reqDto.getParsedBornDate());
                userDetails.setEmailAddress(email);
                final LocationAddressEntity locationAddress = modelMapper.map(reqDto, LocationAddressEntity.class);
                locationAddress.setApartmentNr(StringUtils.trimToNull(reqDto.getApartmentNr()));
                final EmployerEntity employer = EmployerEntity.builder()
                    .login(login)
                    .password(passowordDecoded)
                    .hiredDate(reqDto.getParsedHiredDate())
                    .locationAddress(locationAddress)
                    .userDetails(userDetails)
                    .role(role)
                    .build();

                commandPerformer.createMailbox(email, mailPassword);
                final var adminDetails = (LoggedUserDataDto) httpSession
                    .getAttribute(SessionAttribute.LOGGED_USER_DETAILS.getName());
                final var employerMailDetails = new AddEmployerMailPayload(reqDto, email, mailPassword);

                final MailRequestPayload creatorPayload = MailRequestPayload.builder()
                    .messageResponder(adminDetails.getFullName())
                    .subject("SkiRent Service | Dodanie nowego pracownika " + employerMailDetails.getFullName())
                    .templateName("add-new-employer-creator.template.ftl")
                    .templateVars(Map.of("employer", employerMailDetails))
                    .build();
                final MailRequestPayload requesterPayload = MailRequestPayload.builder()
                    .messageResponder(employerMailDetails.getFullName())
                    .subject("SkiRent Service | Witamy w systemie ")
                    .templateName("add-new-employer-requester.template.ftl")
                    .templateVars(Map.of("employerLogin", login, "employerPassword", passwordRaw))
                    .build();

                mailSocket.sendMessage(adminDetails.getEmailAddress(), creatorPayload, req);
                mailSocket.sendMessage(email, requesterPayload, req);

                session.persist(employer);
                session.getTransaction().commit();
                LOGGER.info("Employer with mailbox was successfuly created. User data: {}", employer);
                alert.setMessage(
                    "Nastąpiło pomyślnie dodanie nowego pracownika. Na adres email <strong>" + email + "</strong> " +
                    "zostało wysłane hasło dostępu do konta. Hasło dostępu do skrzynki email użytkownika znajdziesz " +
                    "w przysłanej na Twój adres email wiadomości."
                );
                alert.setType(AlertType.INFO);
                httpSession.setAttribute(SessionAlert.OWNER_EMPLOYERS_PAGE_ALERT.getName(), alert);
                httpSession.removeAttribute(getClass().getName());
                res.sendRedirect("/owner/employers");
            } catch (RuntimeException ex) {
                if (!StringUtils.isEmpty(email)) commandPerformer.deleteMailbox(email);
                Utils.onHibernateException(session, LOGGER, ex);
            }
        } catch (RuntimeException ex) {
            alert.setMessage(ex.getMessage());
            httpSession.setAttribute(getClass().getName(), resDto);
            httpSession.setAttribute(SessionAlert.OWNER_ADD_EMPLOYER_PAGE_ALERT.getName(), alert);
            LOGGER.error("Unable to create employer. Cause: {}", ex.getMessage());
            res.sendRedirect("/owner/add-employer");
        }
    }
}
