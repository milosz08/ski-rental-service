/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 * Silesian University of Technology
 *
 *  File name: OwnerDeleteEquipmentTypeServlet.java
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
import pl.polsl.skirentalservice.dao.equipment_type.*;
import pl.polsl.skirentalservice.dto.attribute.AttributeModalResDto;

import static org.apache.commons.lang3.StringUtils.defaultIfBlank;

import static pl.polsl.skirentalservice.util.Utils.*;
import static pl.polsl.skirentalservice.util.AlertType.INFO;
import static pl.polsl.skirentalservice.exception.NotFoundException.*;
import static pl.polsl.skirentalservice.exception.AlreadyExistException.*;
import static pl.polsl.skirentalservice.core.db.HibernateUtil.getSessionFactory;
import static pl.polsl.skirentalservice.util.SessionAttribute.EQ_TYPES_MODAL_DATA;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@WebServlet("/owner/delete-equipment-type")
public class OwnerDeleteEquipmentTypeServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(OwnerDeleteEquipmentTypeServlet.class);
    private final SessionFactory sessionFactory = getSessionFactory();

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final String typeId = req.getParameter("id");
        final String loggedUser = getLoggedUserLogin(req);

        final AlertTupleDto alert = new AlertTupleDto(true);
        final HttpSession httpSession = req.getSession();
        final AttributeModalResDto resDto = new AttributeModalResDto();

        resDto.setAlert(alert);
        resDto.setModalImmediatelyOpen(true);

        try (final Session session = sessionFactory.openSession()) {
            try {
                session.beginTransaction();
                final IEquipmentTypeDao equipmentDetailsDao = new EquipmentTypeDao(session);

                final String deletedType = equipmentDetailsDao.getEquipmentTypeNameById(typeId).orElseThrow(() -> {
                    throw new EquipmentTypeNotFoundException();
                });

                resDto.getActiveFirstPage().setActive(false);
                resDto.getActiveSecondPage().setActive(true);

                if (equipmentDetailsDao.checkIfEquipmentTypeHasAnyConnections(typeId)) {
                    throw new EquipmentTypeHasConnectionsException();
                }
                equipmentDetailsDao.deleteEquipmentTypeById(typeId);

                alert.setType(INFO);
                alert.setMessage(
                    "Usuwanie typu sprzętu narciarskiego: <strong>" + deletedType + "</strong> zakończone sukcesem."
                );
                session.getTransaction().commit();
                LOGGER.info("Successful deleted equipment type by: {}. Type: {}", loggedUser, deletedType);
            } catch (RuntimeException ex) {
                onHibernateException(session, LOGGER, ex);
            }
        } catch (RuntimeException ex) {
            alert.setActive(true);
            alert.setMessage(ex.getMessage());
            LOGGER.error("Failure delete equipment type by: {}. Cause: {}", loggedUser, ex.getMessage());
        }
        httpSession.setAttribute(EQ_TYPES_MODAL_DATA.getName(), resDto);
        res.sendRedirect(defaultIfBlank(req.getParameter("redirect"), "/owner/add-equipment"));
    }
}
