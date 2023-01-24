/*
 * Copyright (c) 2023 by multiple authors
 * Silesian University of Technology
 *
 *  File name: AddEquipmentServlet.java
 *  Last modified: 23/01/2023, 18:57
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.domain.owner.equipment;

import org.slf4j.*;
import org.hibernate.Session;

import jakarta.ejb.EJB;
import jakarta.servlet.http.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;

import pl.polsl.skirentalservice.dto.*;
import pl.polsl.skirentalservice.dto.equipment.*;
import pl.polsl.skirentalservice.core.ValidatorBean;
import pl.polsl.skirentalservice.core.db.HibernateBean;
import pl.polsl.skirentalservice.dto.attribute.AttributeModalResDto;

import java.util.*;
import java.io.IOException;

import static pl.polsl.skirentalservice.util.Utils.*;
import static pl.polsl.skirentalservice.exception.ServletException.*;
import static pl.polsl.skirentalservice.util.SessionAlert.OWNER_ADD_EQUIPMENT_PAGE_ALERT;
import static pl.polsl.skirentalservice.util.SessionAttribute.EQUIPMENT_TYPES_MODAL_DATA;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@WebServlet("/owner/add-equipment")
public class OwnerAddEquipmentServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(OwnerAddEquipmentServlet.class);

    @EJB private HibernateBean database;
    @EJB private ValidatorBean validator;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final AlertTupleDto alert = getAndDestroySessionAlert(req, OWNER_ADD_EQUIPMENT_PAGE_ALERT);
        final AttributeModalResDto eqTypeResDto = getAndDestroySessionModalData(req, EQUIPMENT_TYPES_MODAL_DATA);

        final HttpSession httpSession = req.getSession();
        AddEditEquipmentResDto resDto = (AddEditEquipmentResDto) httpSession.getAttribute("response-test");
        httpSession.removeAttribute("response-test");
        if (Objects.isNull(resDto)) resDto = new AddEditEquipmentResDto();

        try (final Session session = database.open()) {
            try {
                session.beginTransaction();

                final String jpqlFindEquipmentTypes = "SELECT new pl.polsl.skirentalservice.dto.FormSelectTupleDto(" +
                        "CAST(t.id AS string), t.name) FROM EquipmentTypeEntity t ORDER BY t.id";
                final List<FormSelectTupleDto> equipmentTypes = session
                    .createQuery(jpqlFindEquipmentTypes, FormSelectTupleDto.class)
                    .getResultList();
                resDto.insertTypesSelects(equipmentTypes);

                final String jpqlFindEquipmentBrands = "SELECT new pl.polsl.skirentalservice.dto.FormSelectTupleDto(" +
                        "CAST(t.id AS string), t.name) FROM EquipmentBrandEntity t ORDER BY t.id";
                final List<FormSelectTupleDto> equipmentBrands = session
                    .createQuery(jpqlFindEquipmentBrands, FormSelectTupleDto.class)
                    .getResultList();
                resDto.insertBrandsSelects(equipmentBrands);

                final String jpqlFindEquipmentColors = "SELECT new pl.polsl.skirentalservice.dto.FormSelectTupleDto(" +
                        "CAST(t.id AS string), t.name) FROM EquipmentColorEntity t ORDER BY t.id";
                final List<FormSelectTupleDto> equipmentColors = session
                    .createQuery(jpqlFindEquipmentColors, FormSelectTupleDto.class)
                    .getResultList();
                resDto.insertColorsSelects(equipmentColors);

                session.getTransaction().commit();
            } catch (RuntimeException ex) {
                if (session.getTransaction().isActive()) {
                    LOGGER.error("Some issues appears. Transaction rollback and revert previous state...");
                    session.getTransaction().rollback();
                }
                throw ex;
            }
        } catch (RuntimeException ex) {
            if (!(ex instanceof InvalidFieldsExistsException)) {
                alert.setActive(true);
                alert.setMessage(ex.getMessage());
            }
        }
        req.setAttribute("alertData", alert);
        req.setAttribute("addEditEquipmentData", resDto);
        req.setAttribute("addEditText", "Dodaj");
        req.setAttribute(EQUIPMENT_TYPES_MODAL_DATA.getName(), eqTypeResDto);
        req.getRequestDispatcher("/WEB-INF/pages/owner/equipment/owner-add-edit-equipment.jsp").forward(req, res);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final HttpSession httpSession = req.getSession();
        final AlertTupleDto alert = new AlertTupleDto(true);

        final AddEditEquipmentReqDto reqDto = new AddEditEquipmentReqDto(req);
        final AddEditEquipmentResDto resDto = new AddEditEquipmentResDto(validator, reqDto);
        if (validator.someFieldsAreInvalid(reqDto)) {
            httpSession.setAttribute("response-test", resDto);
            res.sendRedirect("/owner/add-equipment");
            return;
        }
        try (final Session session = database.open()) {
            try {
                session.beginTransaction();

                // TODO: dodawnie nowych sprzętów do bazy danych + walidacja

                session.getTransaction().commit();
            } catch (RuntimeException ex) {
                if (session.getTransaction().isActive()) {
                    LOGGER.error("Some issues appears. Transaction rollback and revert previous state...");
                    session.getTransaction().rollback();
                }
                throw ex;
            }
        } catch (RuntimeException ex) {
            alert.setMessage(ex.getMessage());
            httpSession.setAttribute(OWNER_ADD_EQUIPMENT_PAGE_ALERT.getName(), alert);
        }
        res.sendRedirect("/owner/add-equipment");
    }
}
