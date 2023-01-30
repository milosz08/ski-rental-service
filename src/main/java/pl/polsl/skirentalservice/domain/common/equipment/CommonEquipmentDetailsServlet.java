/*
 * Copyright (c) 2023 by multiple authors
 * Silesian University of Technology
 *
 *  File name: OwnerEquipmentDetailsServlet.java
 *  Last modified: 27/01/2023, 12:00
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.domain.common.equipment;

import org.slf4j.*;
import org.hibernate.*;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.annotation.WebServlet;

import java.io.IOException;

import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.dto.login.LoggedUserDataDto;
import pl.polsl.skirentalservice.dto.equipment.EquipmentDetailsResDto;

import static java.util.Objects.isNull;

import static pl.polsl.skirentalservice.exception.NotFoundException.*;
import static pl.polsl.skirentalservice.util.Utils.onHibernateException;
import static pl.polsl.skirentalservice.core.db.HibernateUtil.getSessionFactory;
import static pl.polsl.skirentalservice.util.SessionAttribute.LOGGED_USER_DETAILS;
import static pl.polsl.skirentalservice.util.PageTitle.COMMON_EQUIPMENT_DETAILS_PAGE;
import static pl.polsl.skirentalservice.util.SessionAlert.COMMON_EQUIPMENTS_PAGE_ALERT;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@WebServlet(urlPatterns = { "/owner/equipment-details", "/seller/equipment-details" })
public class CommonEquipmentDetailsServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommonEquipmentDetailsServlet.class);
    private final SessionFactory sessionFactory = getSessionFactory();

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final String equipmentId = req.getParameter("id");
        final HttpSession httpSession = req.getSession();
        final var userDataDto = (LoggedUserDataDto) httpSession.getAttribute(LOGGED_USER_DETAILS.getName());

        final AlertTupleDto alert = new AlertTupleDto(true);
        try (final Session session = sessionFactory.openSession()) {
            try {
                session.beginTransaction();

                final String jpqlFindEquipmentDetails =
                    "SELECT new pl.polsl.skirentalservice.dto.equipment.EquipmentDetailsResDto(" +
                        "e.id, e.name, t.name, e.model, e.gender, e.barcode," +
                        "CASE WHEN e.size IS NULL THEN '<i>brak danych</i>' ELSE CAST(e.size AS string) END," +
                        "b.name, c.name, CONCAT(CAST(e.countInStore - SUM(ed.count) AS string), '/', e.countInStore)," +
                        "CAST(SUM(ed.count) AS string), e.pricePerHour, e.priceForNextHour," +
                        "e.pricePerDay, e.valueCost," +
                        "CASE WHEN e.description IS NULL THEN '<i>brak danych</i>' ELSE e.description END" +
                    ") FROM EquipmentEntity e " +
                    "LEFT OUTER JOIN e.rentsEquipments ed " +
                    "INNER JOIN e.equipmentType t INNER JOIN e.equipmentBrand b INNER JOIN e.equipmentColor c " +
                    "WHERE e.id = :eid GROUP BY e.id";
                final EquipmentDetailsResDto equipmentDetails = session
                    .createQuery(jpqlFindEquipmentDetails, EquipmentDetailsResDto.class)
                    .setParameter("eid", equipmentId)
                    .getSingleResultOrNull();
                if (isNull(equipmentDetails)) throw new EquipmentNotFoundException(equipmentId);

                session.getTransaction().commit();
                req.setAttribute("equipmentData", equipmentDetails);
                req.setAttribute("title", COMMON_EQUIPMENT_DETAILS_PAGE.getName());
                req.getRequestDispatcher("/WEB-INF/pages/" + userDataDto.getRoleEng() + "/equipment/" +
                    userDataDto.getRoleEng() + "-equipment-details.jsp").forward(req, res);
            } catch (RuntimeException ex) {
                onHibernateException(session, LOGGER, ex);
            }
        } catch (RuntimeException ex) {
            alert.setMessage(ex.getMessage());
            httpSession.setAttribute(COMMON_EQUIPMENTS_PAGE_ALERT.getName(), alert);
            res.sendRedirect("/" + userDataDto.getRoleEng() + "/equipments");
        }
    }
}
