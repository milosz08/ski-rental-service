/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.domain.owner.attribute;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import pl.polsl.skirentalservice.core.db.HibernateDbSingleton;
import pl.polsl.skirentalservice.dao.EquipmentColorDao;
import pl.polsl.skirentalservice.dao.hibernate.EquipmentColorDaoHib;
import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.dto.attribute.AttributeModalResDto;
import pl.polsl.skirentalservice.util.AlertType;
import pl.polsl.skirentalservice.util.SessionAttribute;
import pl.polsl.skirentalservice.util.Utils;

import java.io.IOException;

import static pl.polsl.skirentalservice.exception.AlreadyExistException.EquipmentColorHasConnectionsException;
import static pl.polsl.skirentalservice.exception.NotFoundException.EquipmentColorNotFoundException;

@Slf4j
@WebServlet("/owner/delete-equipment-color")
public class OwnerDeleteEquipmentColorServlet extends HttpServlet {
    private final SessionFactory sessionFactory = HibernateDbSingleton.getInstance().getSessionFactory();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final String colorId = req.getParameter("id");
        final String loggedUser = Utils.getLoggedUserLogin(req);

        final AlertTupleDto alert = new AlertTupleDto(true);
        final HttpSession httpSession = req.getSession();
        final AttributeModalResDto resDto = new AttributeModalResDto();

        resDto.setAlert(alert);
        resDto.setModalImmediatelyOpen(true);

        try (final Session session = sessionFactory.openSession()) {
            try {
                session.beginTransaction();
                final EquipmentColorDao equipmentDetailsDao = new EquipmentColorDaoHib(session);

                final String deletedColor = equipmentDetailsDao.getEquipmentColorNameById(colorId)
                    .orElseThrow(EquipmentColorNotFoundException::new);

                resDto.getActiveFirstPage().setActive(false);
                resDto.getActiveSecondPage().setActive(true);

                if (equipmentDetailsDao.checkIfEquipmentColorHasAnyConnections(colorId)) {
                    throw new EquipmentColorHasConnectionsException();
                }
                equipmentDetailsDao.deleteEquipmentColorById(colorId);

                alert.setType(AlertType.INFO);
                alert.setMessage(
                    "Usuwanie koloru sprzętu narciarskiego: <strong>" + deletedColor + "</strong> zakończone sukcesem."
                );
                session.getTransaction().commit();
                log.info("Successful deleted equipment color by: {}. Color: {}", loggedUser, deletedColor);
            } catch (RuntimeException ex) {
                Utils.onHibernateException(session, log, ex);
            }
        } catch (RuntimeException ex) {
            alert.setActive(true);
            alert.setMessage(ex.getMessage());
            log.error("Failure delete equipment color by: {}. Cause: {}", loggedUser, ex.getMessage());
        }
        httpSession.setAttribute(SessionAttribute.EQ_COLORS_MODAL_DATA.getName(), resDto);
        res.sendRedirect(StringUtils.defaultIfBlank(req.getParameter("redirect"), "/owner/add-equipment"));
    }
}
