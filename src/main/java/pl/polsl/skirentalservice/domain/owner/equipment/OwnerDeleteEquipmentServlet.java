/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.domain.owner.equipment;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import pl.polsl.skirentalservice.core.ConfigSingleton;
import pl.polsl.skirentalservice.core.db.HibernateDbSingleton;
import pl.polsl.skirentalservice.dao.EquipmentDao;
import pl.polsl.skirentalservice.dao.hibernate.EquipmentDaoHib;
import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.entity.EquipmentEntity;
import pl.polsl.skirentalservice.util.AlertType;
import pl.polsl.skirentalservice.util.SessionAlert;
import pl.polsl.skirentalservice.util.Utils;

import java.io.File;
import java.io.IOException;

import static pl.polsl.skirentalservice.exception.AlreadyExistException.EquipmenHasOpenedRentsException;
import static pl.polsl.skirentalservice.exception.NotFoundException.EquipmentNotFoundException;

@Slf4j
@WebServlet("/owner/delete-equipment")
public class OwnerDeleteEquipmentServlet extends HttpServlet {
    private final SessionFactory sessionFactory = HibernateDbSingleton.getInstance().getSessionFactory();
    private final S3ClientSigleton s3Client = S3ClientSigleton.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final String equipmentId = req.getParameter("id");
        final AlertTupleDto alert = new AlertTupleDto(true);
        final String loggedUser = Utils.getLoggedUserLogin(req);

        final HttpSession httpSession = req.getSession();
        try (final Session session = sessionFactory.openSession()) {
            try {
                session.beginTransaction();
                final EquipmentDao equipmentDao = new EquipmentDaoHib(session);

                if (equipmentDao.checkIfEquipmentHasOpenedRents(equipmentId)) {
                    throw new EquipmenHasOpenedRentsException();
                }
                final EquipmentEntity equipmentEntity = session.getReference(EquipmentEntity.class, equipmentId);
                if (equipmentEntity == null) {
                    throw new EquipmentNotFoundException(equipmentId);
                }
                session.remove(equipmentEntity);
                s3Client.deleteObject(S3Bucket.BARCODES, equipmentEntity.getBarcode() + ".png");

                alert.setType(AlertType.INFO);
                alert.setMessage(
                    "Pomyślnie usunięto sprzęt narciarski z ID <strong>#" + equipmentId + "</strong> z systemu."
                );
                session.getTransaction().commit();
                log.info("Equipment with id: {} was succesfuly removed from system by {}.", equipmentId, loggedUser);
            } catch (RuntimeException ex) {
                Utils.onHibernateException(session, log, ex);
            }
        } catch (RuntimeException ex) {
            alert.setMessage(ex.getMessage());
            log.error("Unable to remove equipment with id: {} by: {}. Cause: {}", loggedUser, equipmentId,
                ex.getMessage());
        }
        httpSession.setAttribute(SessionAlert.COMMON_EQUIPMENTS_PAGE_ALERT.getName(), alert);
        res.sendRedirect("/owner/equipments");
    }
}
