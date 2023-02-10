/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 * Silesian University of Technology
 *
 *  File name: OwnerDeleteEquipmentServlet.java
 *  Last modified: 06/02/2023, 19:49
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.domain.owner.equipment;

import org.slf4j.*;
import org.hibernate.*;

import jakarta.ejb.EJB;
import jakarta.servlet.http.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;

import java.io.File;
import java.io.IOException;

import pl.polsl.skirentalservice.core.ConfigBean;
import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.entity.EquipmentEntity;

import static java.io.File.separator;
import static java.util.Objects.isNull;

import static pl.polsl.skirentalservice.util.Utils.*;
import static pl.polsl.skirentalservice.util.AlertType.INFO;
import static pl.polsl.skirentalservice.util.RentStatus.RETURNED;
import static pl.polsl.skirentalservice.exception.NotFoundException.*;
import static pl.polsl.skirentalservice.exception.AlreadyExistException.*;
import static pl.polsl.skirentalservice.core.db.HibernateUtil.getSessionFactory;
import static pl.polsl.skirentalservice.util.SessionAlert.COMMON_EQUIPMENTS_PAGE_ALERT;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@WebServlet("/owner/delete-equipment")
public class OwnerDeleteEquipmentServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(OwnerDeleteEquipmentServlet.class);
    private final SessionFactory sessionFactory = getSessionFactory();

    @EJB private ConfigBean config;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final String equipmentId = req.getParameter("id");
        final AlertTupleDto alert = new AlertTupleDto(true);
        final String loggedUser = getLoggedUserLogin(req);

        final HttpSession httpSession = req.getSession();
        try (final Session session = sessionFactory.openSession()) {
            try {
                session.beginTransaction();

                final String jpqlFindHasConnections =
                    "SELECT COUNT(r.id) > 0 FROM RentEntity r INNER JOIN r.equipments e INNER JOIN e.equipment eq " +
                    "WHERE eq.id = :eid AND r.status <> :rst";
                final Boolean hasAnyConnections = session.createQuery(jpqlFindHasConnections, Boolean.class)
                    .setParameter("eid", equipmentId).setParameter("rst", RETURNED)
                    .getSingleResult();
                if (hasAnyConnections) throw new EquipmenHasOpenedRentsException();

                final EquipmentEntity equipmentEntity = session.getReference(EquipmentEntity.class, equipmentId);
                if (isNull(equipmentEntity)) throw new EquipmentNotFoundException(equipmentId);

                final String uploadsDir = config.getUploadsDir() + separator + "bar-codes";
                final File barcodeFile = new File(uploadsDir, equipmentEntity.getBarcode() + ".png");
                if (barcodeFile.exists()) {
                    if (!barcodeFile.delete()) throw new RuntimeException("Nieudane usunięcie kodu kreskowego.");
                }
                session.remove(equipmentEntity);
                alert.setType(INFO);
                alert.setMessage(
                    "Pomyślnie usunięto sprzęt narciarski z ID <strong>#" + equipmentId + "</strong> z systemu."
                );
                session.getTransaction().commit();
                LOGGER.info("Equipment with id: {} was succesfuly removed from system by {}.", equipmentId, loggedUser);
            } catch (RuntimeException ex) {
                onHibernateException(session, LOGGER, ex);
            }
        } catch (RuntimeException ex) {
            alert.setMessage(ex.getMessage());
            LOGGER.error("Unable to remove equipment with id: {} by: {}. Cause: {}", loggedUser, equipmentId,
                ex.getMessage());
        }
        httpSession.setAttribute(COMMON_EQUIPMENTS_PAGE_ALERT.getName(), alert);
        res.sendRedirect("/owner/equipments");
    }
}
