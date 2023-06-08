/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: OwnerAddEquipmentColorServlet.java
 * Last modified: 6/3/23, 1:41 AM
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

package pl.polsl.skirentalservice.domain.owner.attribute;

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

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

import pl.polsl.skirentalservice.util.Utils;
import pl.polsl.skirentalservice.util.AlertType;
import pl.polsl.skirentalservice.util.SessionAttribute;
import pl.polsl.skirentalservice.dto.attribute.AttributeValidatorPayloadDto;
import pl.polsl.skirentalservice.core.ValidatorSingleton;
import pl.polsl.skirentalservice.core.db.HibernateDbSingleton;
import pl.polsl.skirentalservice.dao.equipment_color.EquipmentColorDao;
import pl.polsl.skirentalservice.dao.equipment_color.IEquipmentColorDao;
import pl.polsl.skirentalservice.entity.EquipmentColorEntity;

import static pl.polsl.skirentalservice.exception.AlreadyExistException.EquipmentColorAlreadyExistException;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@WebServlet("/owner/add-equipment-color")
public class OwnerAddEquipmentColorServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(OwnerAddEquipmentColorServlet.class);

    private final SessionFactory sessionFactory = HibernateDbSingleton.getInstance().getSessionFactory();
    private final ValidatorSingleton validator = ValidatorSingleton.getInstance();

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final AttributeValidatorPayloadDto payload = Utils.validateEquipmentAttribute(req, validator);
        final String loggedUser = Utils.getLoggedUserLogin(req);
        final HttpSession httpSession = req.getSession();
        if (payload.isInvalid()) {
            httpSession.setAttribute(SessionAttribute.EQ_COLORS_MODAL_DATA.getName(), payload.resDto());
            res.sendRedirect(StringUtils.defaultIfBlank(req.getParameter("redirect"), "/owner/add-equipment"));
            return;
        }
        try (final Session session = sessionFactory.openSession()) {
            try {
                session.beginTransaction();
                final IEquipmentColorDao equipmentDetailsDao = new EquipmentColorDao(session);

                if (equipmentDetailsDao.checkIfEquipmentColorExistByName(payload.reqDto().getName())) {
                    throw new EquipmentColorAlreadyExistException();
                }
                final EquipmentColorEntity colorEntity = new EquipmentColorEntity(payload.reqDto().getName());
                session.persist(colorEntity);

                payload.alert().setType(AlertType.INFO);
                payload.alert().setMessage(
                    "Nastąpiło pomyślne dodanie nowego koloru sprzętu narciarskiego: <strong>" +
                    payload.reqDto().getName() + "</strong>."
                );
                session.getTransaction().commit();
                LOGGER.info("Successful added new equipment color by: {}. Color: {}", loggedUser,
                    payload.reqDto().getName());
            } catch (RuntimeException ex) {
                Utils.onHibernateException(session, LOGGER, ex);
            }
        } catch (RuntimeException ex) {
            payload.alert().setMessage(ex.getMessage());
            LOGGER.error("Failure add new equipment color by: {}. Cause: {}", loggedUser, ex.getMessage());
        }
        payload.alert().setActive(true);
        payload.resDto().setAlert(payload.alert());
        payload.resDto().setModalImmediatelyOpen(true);
        payload.resDto().getName().setValue(StringUtils.EMPTY);
        httpSession.setAttribute(SessionAttribute.EQ_COLORS_MODAL_DATA.getName(), payload.resDto());
        res.sendRedirect(StringUtils.defaultIfBlank(req.getParameter("redirect"), "/owner/add-equipment"));
    }
}
