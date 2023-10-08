/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.domain.seller.deliv_return;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.polsl.skirentalservice.core.ConfigSingleton;
import pl.polsl.skirentalservice.core.db.HibernateDbSingleton;
import pl.polsl.skirentalservice.dao.equipment.EquipmentDao;
import pl.polsl.skirentalservice.dao.equipment.IEquipmentDao;
import pl.polsl.skirentalservice.dao.rent.IRentDao;
import pl.polsl.skirentalservice.dao.rent.RentDao;
import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.entity.RentEquipmentEntity;
import pl.polsl.skirentalservice.entity.RentReturnEntity;
import pl.polsl.skirentalservice.pdf.ReturnPdfDocument;
import pl.polsl.skirentalservice.util.AlertType;
import pl.polsl.skirentalservice.util.RentStatus;
import pl.polsl.skirentalservice.util.SessionAlert;
import pl.polsl.skirentalservice.util.Utils;

import java.io.IOException;
import java.util.Objects;

import static pl.polsl.skirentalservice.exception.NotFoundException.ReturnNotFoundException;

@WebServlet("/seller/delete-return")
public class SellerDeleteReturnServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(SellerDeleteReturnServlet.class);

    private final SessionFactory sessionFactory = HibernateDbSingleton.getInstance().getSessionFactory();
    private final ConfigSingleton config = ConfigSingleton.getInstance();

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
