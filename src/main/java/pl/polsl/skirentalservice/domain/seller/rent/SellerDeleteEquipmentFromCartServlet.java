/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: SellerDeleteEquipmentFromCartServlet.java
 * Last modified: 6/3/23, 12:45 AM
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

package pl.polsl.skirentalservice.domain.seller.rent;

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

import pl.polsl.skirentalservice.util.Utils;
import pl.polsl.skirentalservice.util.AlertType;
import pl.polsl.skirentalservice.util.SessionAlert;
import pl.polsl.skirentalservice.util.SessionAttribute;
import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.dto.rent.InMemoryRentDataDto;
import pl.polsl.skirentalservice.dto.rent.CartSingleEquipmentDataDto;
import pl.polsl.skirentalservice.core.db.HibernateDbSingleton;
import pl.polsl.skirentalservice.dao.equipment.EquipmentDao;
import pl.polsl.skirentalservice.dao.equipment.IEquipmentDao;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.io.IOException;

import static pl.polsl.skirentalservice.exception.NotFoundException.EquipmentNotFoundException;
import static pl.polsl.skirentalservice.exception.NotFoundException.EquipmentInCartNotFoundException;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@WebServlet("/seller/delete-equipment-from-cart")
public class SellerDeleteEquipmentFromCartServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(SellerDeleteEquipmentFromCartServlet.class);
    private final SessionFactory sessionFactory = HibernateDbSingleton.getInstance().getSessionFactory();

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final HttpSession httpSession = req.getSession();
        final var rentData = (InMemoryRentDataDto) httpSession.getAttribute(SessionAttribute.INMEMORY_NEW_RENT_DATA.getName());
        if (Objects.isNull(rentData)) {
            res.sendRedirect("/seller/customers");
            return;
        }
        final String equipmentId = req.getParameter("id");
        if (Objects.isNull(equipmentId)) {
            res.sendRedirect("/seller/complete-rent-equipments");
            return;
        }
        final AlertTupleDto alert = new AlertTupleDto(true);
        final String loggedUser = Utils.getLoggedUserLogin(req);
        try (final Session session = sessionFactory.openSession()) {
            try {
                final IEquipmentDao equipmentDao = new EquipmentDao(session);
                if (!equipmentDao.checkIfEquipmentExist(equipmentId)) throw new EquipmentNotFoundException(equipmentId);

                final CartSingleEquipmentDataDto cartData = rentData.getEquipments().stream()
                    .filter(e -> e.getId().toString().equals(equipmentId)).findFirst()
                    .orElseThrow(EquipmentInCartNotFoundException::new);

                final List<CartSingleEquipmentDataDto> equipmentsWithoutSelected = rentData.getEquipments().stream()
                    .filter(e -> !e.getId().equals(Long.parseLong(equipmentId)))
                    .collect(Collectors.toList());

                rentData.setEquipments(equipmentsWithoutSelected);
                alert.setType(AlertType.INFO);
                alert.setMessage("Pomyślnie usunięto pozycję z listy zestawienia sprzętów kreatora wypożyczania.");
                LOGGER.info("Successfuly deleted equipment from memory-persist data container by: {}. Data: {}",
                    loggedUser, cartData);
            } catch (RuntimeException ex) {
                Utils.onHibernateException(session, LOGGER, ex);
            }
        } catch (RuntimeException ex) {
            alert.setMessage(ex.getMessage());
            LOGGER.error("Failure delete equipment from memory-persist data container by: {}. Cause: {}", loggedUser,
                ex.getMessage());
        }
        httpSession.setAttribute(SessionAlert.SELLER_COMPLETE_RENT_PAGE_ALERT.getName(), alert);
        res.sendRedirect("/seller/complete-rent-equipments");
    }
}
