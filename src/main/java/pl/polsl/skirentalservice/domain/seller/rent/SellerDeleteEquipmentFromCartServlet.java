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
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import pl.polsl.skirentalservice.core.db.HibernateDbSingleton;
import pl.polsl.skirentalservice.dao.EquipmentDao;
import pl.polsl.skirentalservice.dao.hibernate.EquipmentDaoHib;
import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.dto.rent.CartSingleEquipmentDataDto;
import pl.polsl.skirentalservice.dto.rent.InMemoryRentDataDto;
import pl.polsl.skirentalservice.util.AlertType;
import pl.polsl.skirentalservice.util.SessionAlert;
import pl.polsl.skirentalservice.util.SessionAttribute;
import pl.polsl.skirentalservice.util.Utils;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static pl.polsl.skirentalservice.exception.NotFoundException.EquipmentInCartNotFoundException;
import static pl.polsl.skirentalservice.exception.NotFoundException.EquipmentNotFoundException;

@Slf4j
@WebServlet("/seller/delete-equipment-from-cart")
public class SellerDeleteEquipmentFromCartServlet extends HttpServlet {
    private final SessionFactory sessionFactory = HibernateDbSingleton.getInstance().getSessionFactory();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final HttpSession httpSession = req.getSession();
        final var rentData = (InMemoryRentDataDto) httpSession.getAttribute(SessionAttribute.INMEMORY_NEW_RENT_DATA.getName());
        if (rentData == null) {
            res.sendRedirect("/seller/customers");
            return;
        }
        final String equipmentId = req.getParameter("id");
        if (equipmentId == null) {
            res.sendRedirect("/seller/complete-rent-equipments");
            return;
        }
        final AlertTupleDto alert = new AlertTupleDto(true);
        final String loggedUser = Utils.getLoggedUserLogin(req);
        try (final Session session = sessionFactory.openSession()) {
            try {
                final EquipmentDao equipmentDao = new EquipmentDaoHib(session);
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
                log.info("Successfuly deleted equipment from memory-persist data container by: {}. Data: {}",
                    loggedUser, cartData);
            } catch (RuntimeException ex) {
                Utils.onHibernateException(session, log, ex);
            }
        } catch (RuntimeException ex) {
            alert.setMessage(ex.getMessage());
            log.error("Failure delete equipment from memory-persist data container by: {}. Cause: {}", loggedUser,
                ex.getMessage());
        }
        httpSession.setAttribute(SessionAlert.SELLER_COMPLETE_RENT_PAGE_ALERT.getName(), alert);
        res.sendRedirect("/seller/complete-rent-equipments");
    }
}
