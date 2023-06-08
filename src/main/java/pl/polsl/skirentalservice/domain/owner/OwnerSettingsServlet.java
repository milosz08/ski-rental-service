/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: OwnerSettingsServlet.java
 * Last modified: 6/3/23, 12:31 AM
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

package pl.polsl.skirentalservice.domain.owner;

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

import java.util.Objects;
import java.io.IOException;

import pl.polsl.skirentalservice.util.*;
import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.dto.login.LoggedUserDataDto;
import pl.polsl.skirentalservice.dto.employer.AddEditEmployerReqDto;
import pl.polsl.skirentalservice.dto.employer.AddEditEmployerResDto;
import pl.polsl.skirentalservice.core.ConfigSingleton;
import pl.polsl.skirentalservice.core.ValidatorSingleton;
import pl.polsl.skirentalservice.core.db.HibernateDbSingleton;
import pl.polsl.skirentalservice.core.ModelMapperGenerator;
import pl.polsl.skirentalservice.dao.employer.EmployerDao;
import pl.polsl.skirentalservice.dao.employer.IEmployerDao;
import pl.polsl.skirentalservice.dao.user_details.UserDetailsDao;
import pl.polsl.skirentalservice.dao.user_details.IUserDetailsDao;
import pl.polsl.skirentalservice.entity.EmployerEntity;

import static pl.polsl.skirentalservice.exception.NotFoundException.UserNotFoundException;
import static pl.polsl.skirentalservice.exception.AlreadyExistException.PeselAlreadyExistException;
import static pl.polsl.skirentalservice.exception.AlreadyExistException.PhoneNumberAlreadyExistException;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@WebServlet("/owner/settings")
public class OwnerSettingsServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(OwnerSettingsServlet.class);

    private final SessionFactory sessionFactory = HibernateDbSingleton.getInstance().getSessionFactory();
    private final ValidatorSingleton validator = ValidatorSingleton.getInstance();
    private final ConfigSingleton config = ConfigSingleton.getInstance();

    private final ModelMapper modelMapper = ModelMapperGenerator.getModelMapper();

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final HttpSession httpSession = req.getSession();

        final AlertTupleDto alert = Utils.getAndDestroySessionAlert(req, SessionAlert.OWNER_SETTINGS_PAGE_ALERT);
        var resDto = (AddEditEmployerResDto) httpSession.getAttribute(getClass().getName());
        final var ownerDetailsDto = (LoggedUserDataDto) httpSession
            .getAttribute(SessionAttribute.LOGGED_USER_DETAILS.getName());

        if (Objects.isNull(resDto)) {
            try (final Session session = sessionFactory.openSession()) {
                try {
                    session.beginTransaction();
                    final IEmployerDao employerDao = new EmployerDao(session);

                    final var employerDetails = employerDao.findEmployerEditPageDetails(ownerDetailsDto.getId())
                        .orElseThrow(() -> new UserNotFoundException(UserRole.OWNER));
                    resDto = new AddEditEmployerResDto(validator, employerDetails);

                    session.getTransaction().commit();
                } catch (RuntimeException ex) {
                    if (!Objects.isNull(session)) Utils.onHibernateException(session, LOGGER, ex);
                }
            } catch (RuntimeException ex) {
                alert.setMessage(ex.getMessage());
                httpSession.setAttribute(SessionAlert.OWNER_SETTINGS_PAGE_ALERT.getName(), alert);
            }
        }
        req.setAttribute("alertData", alert);
        req.setAttribute("addEditOwnerData", resDto);
        req.setAttribute("addEditText", "Edytuj");
        req.setAttribute("title", PageTitle.OWNER_SETTINGS_PAGE.getName());
        req.getRequestDispatcher("/WEB-INF/pages/owner/owner-settings.jsp").forward(req, res);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final AlertTupleDto alert = new AlertTupleDto(true);
        final HttpSession httpSession = req.getSession();
        final var ownerDetailsDto = (LoggedUserDataDto) httpSession
            .getAttribute(SessionAttribute.LOGGED_USER_DETAILS.getName());

        final AddEditEmployerReqDto reqDto = new AddEditEmployerReqDto(req);
        final AddEditEmployerResDto resDto = new AddEditEmployerResDto(validator, reqDto);
        if (validator.someFieldsAreInvalid(reqDto)) {
            httpSession.setAttribute(getClass().getName(), resDto);
            res.sendRedirect("/owner/settings");
            return;
        }
        try (final Session session = sessionFactory.openSession()) {
            reqDto.validateDates(config);
            try {
                session.beginTransaction();

                final IUserDetailsDao userDetailsDao = new UserDetailsDao(session);

                final EmployerEntity updatableOwner = session.get(EmployerEntity.class, ownerDetailsDto.getId());
                if (Objects.isNull(updatableOwner)) throw new UserNotFoundException(httpSession.getId());

                if (userDetailsDao.checkIfEmployerWithSamePeselExist(reqDto.getPesel(), ownerDetailsDto.getId())) {
                    throw new PeselAlreadyExistException(reqDto.getPesel(), UserRole.OWNER);
                }
                if (userDetailsDao.checkIfEmployerWithSamePhoneNumberExist(reqDto.getPhoneNumber(), ownerDetailsDto.getId())) {
                    throw new PhoneNumberAlreadyExistException(reqDto.getPhoneNumber(), UserRole.OWNER);
                }

                ModelMapperGenerator.onUpdateNullableTransactTurnOn();
                modelMapper.map(reqDto, updatableOwner.getUserDetails());
                modelMapper.map(reqDto, updatableOwner.getLocationAddress());
                modelMapper.map(reqDto, updatableOwner);
                ModelMapperGenerator.onUpdateNullableTransactTurnOff();

                session.getTransaction().commit();

                alert.setType(AlertType.INFO);
                httpSession.removeAttribute(getClass().getName());
                alert.setMessage("Twoje dane osobowe zostały pomyślnie zaktualizowane.");
                httpSession.setAttribute(SessionAlert.COMMON_PROFILE_PAGE_ALERT.getName(), alert);
                LOGGER.info("Owner with id: {} was successfuly updated. Data: {}", ownerDetailsDto.getId(), reqDto);
                res.sendRedirect("/owner/profile");
            } catch (RuntimeException ex) {
                Utils.onHibernateException(session, LOGGER, ex);
            }
        } catch (RuntimeException ex) {
            alert.setMessage(ex.getMessage());
            httpSession.setAttribute(getClass().getName(), resDto);
            httpSession.setAttribute(SessionAlert.OWNER_SETTINGS_PAGE_ALERT.getName(), alert);
            LOGGER.error("Unable to update owner personal data with id: {}. Cause: {}", ownerDetailsDto.getId(),
                ex.getMessage());
            res.sendRedirect("/owner/settings");
        }
    }
}
