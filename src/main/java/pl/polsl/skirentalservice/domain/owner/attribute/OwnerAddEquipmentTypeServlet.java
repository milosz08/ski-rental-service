/*
 * Copyright (c) 2023 by multiple authors
 * Silesian University of Technology
 *
 *  File name: OwnerAddEquipmentTypeServlet.java
 *  Last modified: 24/01/2023, 13:01
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

import pl.polsl.skirentalservice.dto.attribute.*;
import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.core.ValidatorBean;
import pl.polsl.skirentalservice.core.db.HibernateBean;
import pl.polsl.skirentalservice.entity.EquipmentTypeEntity;

import static pl.polsl.skirentalservice.exception.AlreadyExistException.*;

import static pl.polsl.skirentalservice.util.AlertType.INFO;
import static pl.polsl.skirentalservice.util.SessionAlert.OWNER_ADD_EQUIPMENT_PAGE_ALERT;
import static pl.polsl.skirentalservice.util.SessionAttribute.EQUIPMENT_TYPES_MODAL_DATA;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@WebServlet("/owner/add-equipment-type")
public class OwnerAddEquipmentTypeServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(OwnerDeleteEquipmentTypeServlet.class);

    @EJB private HibernateBean database;
    @EJB private ValidatorBean validator;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final AlertTupleDto failureAlert = new AlertTupleDto();
        final AlertTupleDto successAlert = new AlertTupleDto(true);
        final HttpSession httpSession = req.getSession();

        final AttributeModalReqDto reqDto = new AttributeModalReqDto(req);
        final AttributeModalResDto resDto = new AttributeModalResDto(validator, reqDto, failureAlert);

        resDto.setModalImmediatelyOpen(validator.someFieldsAreInvalid(reqDto));
        if (validator.someFieldsAreInvalid(reqDto)) {
            httpSession.setAttribute(EQUIPMENT_TYPES_MODAL_DATA.getName(), resDto);
            res.sendRedirect("/owner/add-equipment");
            return;
        }
        try (final Session session = database.open()) {
            try {
                session.beginTransaction();

                final String jpqlFindTypeAlreadyExist =
                    "SELECT COUNT(t.id) > 0 FROM EquipmentTypeEntity t WHERE LOWER(t.name) = LOWER(:name)";
                final Boolean typeAlreadyExist = session.createQuery(jpqlFindTypeAlreadyExist, Boolean.class)
                    .setParameter("name", reqDto.getName())
                    .getSingleResult();
                if (typeAlreadyExist) throw new EquipmentTypeAlreadyExistException();

                final EquipmentTypeEntity typeEntity = new EquipmentTypeEntity(reqDto.getName());
                session.persist(typeEntity);

                resDto.getName().setValue("");
                successAlert.setType(INFO);
                successAlert.setMessage(
                    "Nastąpiło pomyślne dodanie nowego typu sprzętu narciarskiego: <strong>" + reqDto.getName()
                    + "</strong>."
                );
                httpSession.setAttribute(OWNER_ADD_EQUIPMENT_PAGE_ALERT.getName(), successAlert);
                session.getTransaction().commit();
            } catch (RuntimeException ex) {
                onHibernateException(session, LOGGER, ex);
            }
        } catch (RuntimeException ex) {
            failureAlert.setActive(true);
            failureAlert.setMessage(ex.getMessage());
            resDto.setAlert(failureAlert);
            resDto.setModalImmediatelyOpen(true);
        }
        httpSession.setAttribute(EQUIPMENT_TYPES_MODAL_DATA.getName(), resDto);
        res.sendRedirect("/owner/add-equipment");
    }
}
