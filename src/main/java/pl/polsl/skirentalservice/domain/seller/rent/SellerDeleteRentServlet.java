/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 * Silesian University of Technology
 *
 *  File name: SellerDeleteRentServlet.java
 *  Last modified: 09/02/2023, 00:49
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.domain.seller.rent;

import org.slf4j.*;
import org.hibernate.*;

import jakarta.ejb.EJB;
import jakarta.servlet.http.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;

import java.io.IOException;

import pl.polsl.skirentalservice.entity.*;
import pl.polsl.skirentalservice.core.ConfigBean;
import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.exception.NotFoundException;
import pl.polsl.skirentalservice.pdf.RentPdfDocument;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.math.NumberUtils.toLong;

import static pl.polsl.skirentalservice.util.Utils.*;
import static pl.polsl.skirentalservice.util.AlertType.INFO;
import static pl.polsl.skirentalservice.util.RentStatus.RETURNED;
import static pl.polsl.skirentalservice.core.db.HibernateUtil.getSessionFactory;
import static pl.polsl.skirentalservice.util.SessionAlert.COMMON_RENTS_PAGE_ALERT;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@WebServlet("/seller/delete-rent")
public class SellerDeleteRentServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(SellerDeleteRentServlet.class);
    private final SessionFactory sessionFactory = getSessionFactory();

    @EJB private ConfigBean config;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final Long rentId = toLong(req.getParameter("id"));
        final AlertTupleDto alert = new AlertTupleDto(true);

        final HttpSession httpSession = req.getSession();
        final String loggedUser = getLoggedUserLogin(req);
        try (final Session session = sessionFactory.openSession()) {
            try {
                session.beginTransaction();

                final RentEntity rentEntity = session.getReference(RentEntity.class, rentId);
                if (isNull(rentEntity)) throw new NotFoundException.RentNotFoundException();
                if (rentEntity.getStatus().equals(RETURNED)) throw new RuntimeException(
                    "Usunięcie wypożyczenia ze statusem <strong>wypożyczone</strong> nie jest możliwe. Aby usunąć " +
                    "niechciane wypożyczenie, usuń przypisany do niego dokument zwrotu."
                );
                for (final RentEquipmentEntity equipment : rentEntity.getEquipments()) {
                    if (isNull(equipment.getEquipment())) continue;
                    final String jpqlIncreaseEquipmentCount =
                        "UPDATE EquipmentEntity e SET e.availableCount = e.availableCount + :rentedCount " +
                        "WHERE e.id = :eid";
                    session.createMutationQuery(jpqlIncreaseEquipmentCount)
                        .setParameter("eid", equipment.getEquipment().getId())
                        .setParameter("rentedCount", equipment.getCount())
                        .executeUpdate();
                }

                final RentPdfDocument rentPdfDocument = new RentPdfDocument(config.getUploadsDir(),
                    rentEntity.getIssuedIdentifier());
                rentPdfDocument.remove();

                session.remove(rentEntity);
                session.getTransaction().commit();
                alert.setType(INFO);
                alert.setMessage(
                    "Pomyślnie usunięto wypożyczenie <strong>" + rentEntity.getIssuedIdentifier() + "</strong> " +
                    "z systemu."
                );
                LOGGER.info("Rent with id: {} was succesfuly removed from system by {}. Rent data: {}", rentId,
                    loggedUser, rentEntity);
            } catch (RuntimeException ex) {
                onHibernateException(session, LOGGER, ex);
            }
        } catch (RuntimeException ex) {
            alert.setMessage(ex.getMessage());
            LOGGER.error("Unable to remove rent with id: {} by {}. Cause: {}", rentId, loggedUser, ex.getMessage());
        }
        httpSession.setAttribute(COMMON_RENTS_PAGE_ALERT.getName(), alert);
        res.sendRedirect("/seller/rents");
    }
}
