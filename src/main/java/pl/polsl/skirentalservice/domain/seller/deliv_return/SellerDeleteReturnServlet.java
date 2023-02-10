/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 * Silesian University of Technology
 *
 *  File name: SellerDeleteReturnServlet.java
 *  Last modified: 09/02/2023, 00:48
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.domain.seller.deliv_return;

import org.slf4j.*;
import org.hibernate.*;

import jakarta.ejb.EJB;
import jakarta.servlet.http.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;

import pl.polsl.skirentalservice.entity.*;
import pl.polsl.skirentalservice.core.ConfigBean;
import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.pdf.ReturnPdfDocument;
import pl.polsl.skirentalservice.domain.seller.rent.SellerDeleteRentServlet;

import java.io.IOException;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.trimToNull;

import static pl.polsl.skirentalservice.util.Utils.*;
import static pl.polsl.skirentalservice.util.AlertType.INFO;
import static pl.polsl.skirentalservice.util.RentStatus.RENTED;
import static pl.polsl.skirentalservice.exception.NotFoundException.*;
import static pl.polsl.skirentalservice.core.db.HibernateUtil.getSessionFactory;
import static pl.polsl.skirentalservice.util.SessionAlert.COMMON_RETURNS_PAGE_ALERT;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@WebServlet("/seller/delete-return")
public class SellerDeleteReturnServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(SellerDeleteRentServlet.class);
    private final SessionFactory sessionFactory = getSessionFactory();

    @EJB private ConfigBean config;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final String returnId = trimToNull(req.getParameter("id"));
        if (isNull(returnId)) {
            res.sendRedirect("/seller/returns");
            return;
        }
        final AlertTupleDto alert = new AlertTupleDto(true);
        final String userLogin = getLoggedUserLogin(req);
        final HttpSession httpSession = req.getSession();

        try (final Session session = sessionFactory.openSession()) {
            try {
                session.beginTransaction();

                final RentReturnEntity rentReturn = session.getReference(RentReturnEntity.class, returnId);
                if (isNull(rentReturn)) throw new ReturnNotFoundException();

                final String jpqlChangeRentStatus = "UPDATE RentEntity r SET r.status = :rstat WHERE r.id = :rentid";
                session.createMutationQuery(jpqlChangeRentStatus)
                    .setParameter("rstat", RENTED).setParameter("rentid", rentReturn.getRent().getId())
                    .executeUpdate();

                for (final RentEquipmentEntity equipment : rentReturn.getRent().getEquipments()) {
                    if (isNull(equipment.getEquipment())) continue;
                    final String jpqlIncreaseEquipmentCount =
                        "UPDATE EquipmentEntity e SET e.availableCount = e.availableCount - :rentedCount " +
                        "WHERE e.id = :eid";
                    session.createMutationQuery(jpqlIncreaseEquipmentCount)
                        .setParameter("eid", equipment.getEquipment().getId())
                        .setParameter("rentedCount", equipment.getCount())
                        .executeUpdate();
                }

                final ReturnPdfDocument returnPdfDocument = new ReturnPdfDocument(config.getUploadsDir(),
                    rentReturn.getIssuedIdentifier());
                returnPdfDocument.remove();

                alert.setType(INFO);
                alert.setMessage(
                    "Usunięcie zwrotu wypożyczenia o numerze <strong>" + rentReturn.getIssuedIdentifier() +
                    "</strong> zakończone pomyślnie."
                );
                session.remove(rentReturn);
                session.getTransaction().commit();
                LOGGER.info("Rent return with id: {} was succesfuly removed from system by {}. Rent data: {}", returnId,
                    userLogin, rentReturn);
            } catch (RuntimeException ex) {
                onHibernateException(session, LOGGER, ex);
            }
        } catch (RuntimeException ex) {
            alert.setMessage(ex.getMessage());
        }
        httpSession.setAttribute(COMMON_RETURNS_PAGE_ALERT.getName(), alert);
        res.sendRedirect("/seller/returns");
    }
}
