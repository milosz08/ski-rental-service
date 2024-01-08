/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.domain.seller.rent;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import pl.polsl.skirentalservice.core.db.HibernateDbSingleton;
import pl.polsl.skirentalservice.core.s3.S3Bucket;
import pl.polsl.skirentalservice.core.s3.S3ClientSigleton;
import pl.polsl.skirentalservice.dao.EquipmentDao;
import pl.polsl.skirentalservice.dao.hibernate.EquipmentDaoHib;
import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.entity.RentEntity;
import pl.polsl.skirentalservice.entity.RentEquipmentEntity;
import pl.polsl.skirentalservice.util.AlertType;
import pl.polsl.skirentalservice.util.RentStatus;
import pl.polsl.skirentalservice.util.SessionAlert;
import pl.polsl.skirentalservice.util.Utils;

import java.io.IOException;

import static pl.polsl.skirentalservice.exception.NotFoundException.RentNotFoundException;

@Slf4j
@WebServlet("/seller/delete-rent")
public class SellerDeleteRentServlet extends HttpServlet {
    private final SessionFactory sessionFactory = HibernateDbSingleton.getInstance().getSessionFactory();
    private final S3ClientSigleton s3Client = S3ClientSigleton.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final Long rentId = NumberUtils.toLong(req.getParameter("id"));
        final AlertTupleDto alert = new AlertTupleDto(true);

        final HttpSession httpSession = req.getSession();
        final String loggedUser = Utils.getLoggedUserLogin(req);
        try (final Session session = sessionFactory.openSession()) {
            try {
                session.beginTransaction();
                final EquipmentDao equipmentDao = new EquipmentDaoHib(session);

                final RentEntity rentEntity = session.getReference(RentEntity.class, rentId);
                if (rentEntity == null) {
                    throw new RentNotFoundException();
                }
                if (rentEntity.getStatus().equals(RentStatus.RETURNED)) throw new RuntimeException(
                    "Usunięcie wypożyczenia ze statusem <strong>wypożyczone</strong> nie jest możliwe. Aby usunąć " +
                        "niechciane wypożyczenie, usuń przypisany do niego dokument zwrotu."
                );
                for (final RentEquipmentEntity equipment : rentEntity.getEquipments()) {
                    if (equipment.getEquipment() == null) {
                        continue;
                    }
                    equipmentDao.increaseAvailableSelectedEquipmentCount(equipment.getEquipment().getId(),
                        equipment.getCount());
                }
                final String fileName = rentEntity.getIssuedIdentifier().replaceAll("/", "-") + ".pdf";
                s3Client.deleteObject(S3Bucket.RENTS, fileName);

                session.remove(rentEntity);
                session.getTransaction().commit();
                alert.setType(AlertType.INFO);
                alert.setMessage(
                    "Pomyślnie usunięto wypożyczenie <strong>" + rentEntity.getIssuedIdentifier() + "</strong> " +
                        "z systemu."
                );
                log.info("Rent with id: {} was succesfuly removed from system by {}. Rent data: {}", rentId,
                    loggedUser, rentEntity);
            } catch (RuntimeException ex) {
                Utils.onHibernateException(session, log, ex);
            }
        } catch (RuntimeException ex) {
            alert.setMessage(ex.getMessage());
            log.error("Unable to remove rent with id: {} by {}. Cause: {}", rentId, loggedUser, ex.getMessage());
        }
        httpSession.setAttribute(SessionAlert.COMMON_RENTS_PAGE_ALERT.getName(), alert);
        res.sendRedirect("/seller/rents");
    }
}
