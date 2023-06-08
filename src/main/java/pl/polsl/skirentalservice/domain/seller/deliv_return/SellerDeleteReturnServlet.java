/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: SellerDeleteReturnServlet.java
 * Last modified: 6/3/23, 12:28 AM
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

package pl.polsl.skirentalservice.domain.seller.deliv_return;

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

import org.apache.commons.lang3.StringUtils;

import pl.polsl.skirentalservice.util.Utils;
import pl.polsl.skirentalservice.util.AlertType;
import pl.polsl.skirentalservice.util.RentStatus;
import pl.polsl.skirentalservice.util.SessionAlert;
import pl.polsl.skirentalservice.core.ConfigSingleton;
import pl.polsl.skirentalservice.core.db.HibernateDbSingleton;
import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.dao.rent.RentDao;
import pl.polsl.skirentalservice.dao.rent.IRentDao;
import pl.polsl.skirentalservice.dao.equipment.EquipmentDao;
import pl.polsl.skirentalservice.dao.equipment.IEquipmentDao;
import pl.polsl.skirentalservice.entity.RentReturnEntity;
import pl.polsl.skirentalservice.entity.RentEquipmentEntity;
import pl.polsl.skirentalservice.pdf.ReturnPdfDocument;

import static pl.polsl.skirentalservice.exception.NotFoundException.ReturnNotFoundException;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@WebServlet("/seller/delete-return")
public class SellerDeleteReturnServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(SellerDeleteReturnServlet.class);

    private final SessionFactory sessionFactory = HibernateDbSingleton.getInstance().getSessionFactory();
    private final ConfigSingleton config = ConfigSingleton.getInstance();

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final String returnId = StringUtils.trimToNull(req.getParameter("id"));
        if (Objects.isNull(returnId)) {
            res.sendRedirect("/seller/returns");
            return;
        }
        final AlertTupleDto alert = new AlertTupleDto(true);
        final String userLogin = Utils.getLoggedUserLogin(req);
        final HttpSession httpSession = req.getSession();

        try (final Session session = sessionFactory.openSession()) {
            try {
                session.beginTransaction();

                final IEquipmentDao equipmentDao = new EquipmentDao(session);
                final IRentDao rentDao = new RentDao(session);

                final RentReturnEntity rentReturn = session.getReference(RentReturnEntity.class, returnId);
                if (Objects.isNull(rentReturn)) throw new ReturnNotFoundException();

                rentDao.updateRentStatus(RentStatus.RENTED, rentReturn.getRent().getId());
                for (final RentEquipmentEntity equipment : rentReturn.getRent().getEquipments()) {
                    if (Objects.isNull(equipment.getEquipment())) continue;
                    equipmentDao.decreaseAvailableSelectedEquipmentCount(equipment.getEquipment().getId(),
                        equipment.getCount());
                }
                final ReturnPdfDocument returnPdfDocument = new ReturnPdfDocument(config.getUploadsDir(),
                    rentReturn.getIssuedIdentifier());
                returnPdfDocument.remove();

                alert.setType(AlertType.INFO);
                alert.setMessage(
                    "Usunięcie zwrotu wypożyczenia o numerze <strong>" + rentReturn.getIssuedIdentifier() +
                    "</strong> zakończone pomyślnie."
                );
                session.remove(rentReturn);
                session.getTransaction().commit();
                LOGGER.info("Rent return with id: {} was succesfuly removed from system by {}. Rent data: {}", returnId,
                    userLogin, rentReturn);
            } catch (RuntimeException ex) {
                Utils.onHibernateException(session, LOGGER, ex);
            }
        } catch (RuntimeException ex) {
            alert.setMessage(ex.getMessage());
        }
        httpSession.setAttribute(SessionAlert.COMMON_RETURNS_PAGE_ALERT.getName(), alert);
        res.sendRedirect("/seller/returns");
    }
}
