/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 * Silesian University of Technology
 *
 *  File name: SellerDeleteEquipmentFromCartServlet.java
 *  Last modified: 30/01/2023, 18:11
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.domain.seller.rent;

import org.slf4j.*;
import org.hibernate.*;

import jakarta.servlet.http.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;

import pl.polsl.skirentalservice.dto.rent.*;
import pl.polsl.skirentalservice.dto.AlertTupleDto;

import java.util.List;
import java.io.IOException;
import java.util.stream.Collectors;

import static java.lang.Long.parseLong;
import static java.util.Objects.isNull;

import static pl.polsl.skirentalservice.util.Utils.*;
import static pl.polsl.skirentalservice.util.AlertType.INFO;
import static pl.polsl.skirentalservice.exception.NotFoundException.*;
import static pl.polsl.skirentalservice.core.db.HibernateUtil.getSessionFactory;
import static pl.polsl.skirentalservice.util.SessionAttribute.INMEMORY_NEW_RENT_DATA;
import static pl.polsl.skirentalservice.util.SessionAlert.SELLER_COMPLETE_RENT_PAGE_ALERT;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@WebServlet("/seller/delete-equipment-from-cart")
public class SellerDeleteEquipmentFromCartServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(SellerDeleteEquipmentFromCartServlet.class);
    private final SessionFactory sessionFactory = getSessionFactory();

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final HttpSession httpSession = req.getSession();
        final var rentData = (InMemoryRentDataDto) httpSession.getAttribute(INMEMORY_NEW_RENT_DATA.getName());
        if (isNull(rentData)) {
            res.sendRedirect("/seller/customers");
            return;
        }
        final String equipmentId = req.getParameter("id");
        if (isNull(equipmentId)) {
            res.sendRedirect("/seller/complete-rent-equipments");
            return;
        }
        final AlertTupleDto alert = new AlertTupleDto(true);
        final String loggedUser = getLoggedUserLogin(req);
        try (final Session session = sessionFactory.openSession()) {
            try {
                final String jpqlEquipmentDetails =
                    "SELECT COUNT(e.id) > 0 FROM EquipmentEntity e INNER JOIN e.equipmentType t WHERE e.id = :id";
                final Boolean equipmentIsExist = session.createQuery(jpqlEquipmentDetails, Boolean.class)
                    .setParameter("id", equipmentId)
                    .getSingleResult();
                if (!equipmentIsExist) throw new EquipmentNotFoundException(equipmentId);

                final CartSingleEquipmentDataDto cartData = rentData.getEquipments().stream()
                    .filter(e -> e.getId().toString().equals(equipmentId)).findFirst()
                    .orElseThrow(() -> { throw new EquipmentInCartNotFoundException(); });

                final List<CartSingleEquipmentDataDto> equipmentsWithoutSelected = rentData.getEquipments().stream()
                    .filter(e -> !e.getId().equals(parseLong(equipmentId)))
                    .collect(Collectors.toList());

                rentData.setEquipments(equipmentsWithoutSelected);
                alert.setType(INFO);
                alert.setMessage("Pomyślnie usunięto pozycję z listy zestawienia sprzętów kreatora wypożyczania.");
                LOGGER.info("Successfuly deleted equipment from memory-persist data container by: {}. Data: {}",
                    loggedUser, cartData);
            } catch (RuntimeException ex) {
                onHibernateException(session, LOGGER, ex);
            }
        } catch (RuntimeException ex) {
            alert.setMessage(ex.getMessage());
            LOGGER.error("Failure delete equipment from memory-persist data container by: {}. Cause: {}", loggedUser,
                ex.getMessage());
        }
        httpSession.setAttribute(SELLER_COMPLETE_RENT_PAGE_ALERT.getName(), alert);
        res.sendRedirect("/seller/complete-rent-equipments");
    }
}
