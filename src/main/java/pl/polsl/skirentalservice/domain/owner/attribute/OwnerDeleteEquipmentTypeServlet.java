/*
 * Copyright (c) 2023 by multiple authors
 * Silesian University of Technology
 *
 *  File name: OwnerDeleteEquipmentTypeServlet.java
 *  Last modified: 24/01/2023, 13:02
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.domain.owner.attribute;

import org.slf4j.*;
import org.hibernate.Session;

import jakarta.ejb.EJB;
import jakarta.servlet.http.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;

import java.io.IOException;

import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.core.db.HibernateBean;
import pl.polsl.skirentalservice.dto.attribute.AttributeModalResDto;

import static java.util.Objects.isNull;

import static pl.polsl.skirentalservice.util.AlertType.INFO;
import static pl.polsl.skirentalservice.exception.NotFoundException.*;
import static pl.polsl.skirentalservice.util.SessionAttribute.EQUIPMENT_TYPES_MODAL_DATA;
import static pl.polsl.skirentalservice.util.Utils.onHibernateException;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@WebServlet("/owner/delete-equipment-type")
public class OwnerDeleteEquipmentTypeServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(OwnerDeleteEquipmentTypeServlet.class);

    @EJB private HibernateBean database;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final String typeId = req.getParameter("id");

        final AlertTupleDto alert = new AlertTupleDto(true);
        final HttpSession httpSession = req.getSession();
        final AttributeModalResDto resDto = new AttributeModalResDto();
        resDto.setAlert(alert);
        resDto.setModalImmediatelyOpen(true);

        try (final Session session = database.open()) {
            try {
                session.beginTransaction();

                final String jqplFindNameOfDeletingType = "SELECT t.name FROM EquipmentTypeEntity t WHERE t.id = :id";
                final String getDeletedName = session.createQuery(jqplFindNameOfDeletingType, String.class)
                        .setParameter("id", typeId).getSingleResultOrNull();
                if (isNull(getDeletedName)) throw new EquipmentTypeNotFoundException();

                // TODO: sprawdzenie przed usunięciem czy nie ma żadnych odwołań w tabeli equipments

                session.createMutationQuery("DELETE EquipmentTypeEntity e WHERE e.id = :id")
                    .setParameter("id", typeId).executeUpdate();

                resDto.getActiveFirstPage().setActive(false);
                resDto.getActiveSecondPage().setActive(true);
                alert.setType(INFO);
                alert.setMessage(
                    "Usuwanie typu sprzętu narciarskiego: <strong>" + getDeletedName + "</strong> zakończone sukcesem."
                );
                session.getTransaction().commit();
            } catch (RuntimeException ex) {
                onHibernateException(session, LOGGER, ex);
            }
        } catch (RuntimeException ex) {
            alert.setActive(true);
            alert.setMessage(ex.getMessage());
            resDto.setAlert(alert);
            resDto.setModalImmediatelyOpen(true);
        }
        httpSession.setAttribute(EQUIPMENT_TYPES_MODAL_DATA.getName(), resDto);
        res.sendRedirect("/owner/add-equipment");
    }
}
