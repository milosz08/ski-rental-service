/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: SellerDeleteRentServlet.java
 * Last modified: 6/3/23, 12:22 AM
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

import java.util.Objects;
import java.io.IOException;

import org.apache.commons.lang3.math.NumberUtils;

import pl.polsl.skirentalservice.util.Utils;
import pl.polsl.skirentalservice.util.AlertType;
import pl.polsl.skirentalservice.util.RentStatus;
import pl.polsl.skirentalservice.util.SessionAlert;
import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.core.ConfigSingleton;
import pl.polsl.skirentalservice.core.db.HibernateDbSingleton;
import pl.polsl.skirentalservice.dao.equipment.EquipmentDao;
import pl.polsl.skirentalservice.dao.equipment.IEquipmentDao;
import pl.polsl.skirentalservice.entity.RentEntity;
import pl.polsl.skirentalservice.entity.RentEquipmentEntity;
import pl.polsl.skirentalservice.pdf.RentPdfDocument;

import static pl.polsl.skirentalservice.exception.NotFoundException.RentNotFoundException;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@WebServlet("/seller/delete-rent")
public class SellerDeleteRentServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(SellerDeleteRentServlet.class);

    private final SessionFactory sessionFactory = HibernateDbSingleton.getInstance().getSessionFactory();
    private final ConfigSingleton config = ConfigSingleton.getInstance();

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final Long rentId = NumberUtils.toLong(req.getParameter("id"));
        final AlertTupleDto alert = new AlertTupleDto(true);

        final HttpSession httpSession = req.getSession();
        final String loggedUser = Utils.getLoggedUserLogin(req);
        try (final Session session = sessionFactory.openSession()) {
            try {
                session.beginTransaction();
                final IEquipmentDao equipmentDao = new EquipmentDao(session);

                final RentEntity rentEntity = session.getReference(RentEntity.class, rentId);
                if (Objects.isNull(rentEntity)) throw new RentNotFoundException();
                if (rentEntity.getStatus().equals(RentStatus.RETURNED)) throw new RuntimeException(
                    "Usunięcie wypożyczenia ze statusem <strong>wypożyczone</strong> nie jest możliwe. Aby usunąć " +
                    "niechciane wypożyczenie, usuń przypisany do niego dokument zwrotu."
                );
                for (final RentEquipmentEntity equipment : rentEntity.getEquipments()) {
                    if (Objects.isNull(equipment.getEquipment())) continue;
                    equipmentDao.increaseAvailableSelectedEquipmentCount(equipment.getEquipment().getId(),
                        equipment.getCount());
                }

                final RentPdfDocument rentPdfDocument = new RentPdfDocument(config.getUploadsDir(),
                    rentEntity.getIssuedIdentifier());
                rentPdfDocument.remove();

                session.remove(rentEntity);
                session.getTransaction().commit();
                alert.setType(AlertType.INFO);
                alert.setMessage(
                    "Pomyślnie usunięto wypożyczenie <strong>" + rentEntity.getIssuedIdentifier() + "</strong> " +
                    "z systemu."
                );
                LOGGER.info("Rent with id: {} was succesfuly removed from system by {}. Rent data: {}", rentId,
                    loggedUser, rentEntity);
            } catch (RuntimeException ex) {
                Utils.onHibernateException(session, LOGGER, ex);
            }
        } catch (RuntimeException ex) {
            alert.setMessage(ex.getMessage());
            LOGGER.error("Unable to remove rent with id: {} by {}. Cause: {}", rentId, loggedUser, ex.getMessage());
        }
        httpSession.setAttribute(SessionAlert.COMMON_RENTS_PAGE_ALERT.getName(), alert);
        res.sendRedirect("/seller/rents");
    }
}
