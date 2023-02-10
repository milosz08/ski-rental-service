/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 * Silesian University of Technology
 *
 *  File name: OwnerDeleteEquipmentColorServlet.java
 *  Last modified: 30/01/2023, 18:14
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.domain.owner.attribute;

import org.slf4j.*;
import org.hibernate.*;

import jakarta.servlet.http.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;

import java.io.IOException;

import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.dto.attribute.AttributeModalResDto;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.defaultIfBlank;

import static pl.polsl.skirentalservice.util.Utils.*;
import static pl.polsl.skirentalservice.util.AlertType.INFO;
import static pl.polsl.skirentalservice.exception.NotFoundException.*;
import static pl.polsl.skirentalservice.exception.AlreadyExistException.*;
import static pl.polsl.skirentalservice.core.db.HibernateUtil.getSessionFactory;
import static pl.polsl.skirentalservice.util.SessionAttribute.EQ_COLORS_MODAL_DATA;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@WebServlet("/owner/delete-equipment-color")
public class OwnerDeleteEquipmentColorServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(OwnerDeleteEquipmentColorServlet.class);
    private final SessionFactory sessionFactory = getSessionFactory();

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final String colorId = req.getParameter("id");
        final String loggedUser = getLoggedUserLogin(req);

        final AlertTupleDto alert = new AlertTupleDto(true);
        final HttpSession httpSession = req.getSession();
        final AttributeModalResDto resDto = new AttributeModalResDto();

        resDto.setAlert(alert);
        resDto.setModalImmediatelyOpen(true);

        try (final Session session = sessionFactory.openSession()) {
            try {
                session.beginTransaction();

                final String jqplFindNameOfDeletingColor = "SELECT c.name FROM EquipmentColorEntity c WHERE c.id = :id";
                final String getDeletedName = session.createQuery(jqplFindNameOfDeletingColor, String.class)
                    .setParameter("id", colorId).getSingleResultOrNull();
                if (isNull(getDeletedName)) throw new EquipmentColorNotFoundException();

                resDto.getActiveFirstPage().setActive(false);
                resDto.getActiveSecondPage().setActive(true);

                final String jpqlFindColorHasConnections =
                    "SELECT COUNT(e.id) > 0 FROM EquipmentEntity e INNER JOIN e.equipmentColor c WHERE c.id = :id";
                final Boolean attributeHasConnections = session.createQuery(jpqlFindColorHasConnections, Boolean.class)
                    .setParameter("id", colorId)
                    .getSingleResult();
                if (attributeHasConnections) throw new EquipmentColorHasConnectionsException();

                session.createMutationQuery("DELETE EquipmentColorEntity c WHERE c.id = :id")
                    .setParameter("id", colorId).executeUpdate();
                alert.setType(INFO);
                alert.setMessage(
                    "Usuwanie koloru sprzętu narciarskiego: <strong>" + getDeletedName + "</strong> zakończone sukcesem."
                );
                session.getTransaction().commit();
                LOGGER.info("Successful deleted equipment color by: {}. Color: {}", loggedUser, getDeletedName);
            } catch (RuntimeException ex) {
                onHibernateException(session, LOGGER, ex);
            }
        } catch (RuntimeException ex) {
            alert.setActive(true);
            alert.setMessage(ex.getMessage());
            LOGGER.error("Failure delete equipment color by: {}. Cause: {}", loggedUser, ex.getMessage());
        }
        httpSession.setAttribute(EQ_COLORS_MODAL_DATA.getName(), resDto);
        res.sendRedirect(defaultIfBlank(req.getParameter("redirect"), "/owner/add-equipment"));
    }
}
