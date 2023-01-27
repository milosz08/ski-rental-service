/*
 * Copyright (c) 2023 by multiple authors
 * Silesian University of Technology
 *
 *  File name: OwnerEditEquipmentServlet.java
 *  Last modified: 26/01/2023, 23:12
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

import pl.polsl.skirentalservice.core.*;
import pl.polsl.skirentalservice.dto.FormSelectTupleDto;
import pl.polsl.skirentalservice.entity.*;
import pl.polsl.skirentalservice.dto.equipment.*;
import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.core.db.HibernateBean;

import java.io.IOException;
import java.util.List;

import static java.util.Objects.isNull;

import static pl.polsl.skirentalservice.util.Utils.*;
import static pl.polsl.skirentalservice.util.SessionAlert.*;
import static pl.polsl.skirentalservice.util.AlertType.INFO;
import static pl.polsl.skirentalservice.util.SessionAttribute.*;
import static pl.polsl.skirentalservice.exception.NotFoundException.*;
import static pl.polsl.skirentalservice.exception.AlreadyExistException.*;
import static pl.polsl.skirentalservice.util.PageTitle.OWNER_EDIT_EQUIPMENT_PAGE;
import static pl.polsl.skirentalservice.util.SessionAttribute.EQ_COLORS_MODAL_DATA;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@WebServlet("/owner/edit-equipment")
public class OwnerEditEquipmentServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(OwnerEditEquipmentServlet.class);

    @EJB private HibernateBean database;
    @EJB private ModelMapperBean modelMapper;
    @EJB private ValidatorBean validator;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final String equipmentId = req.getParameter("id");
        final HttpSession httpSession = req.getSession();

        final AlertTupleDto alert = getAndDestroySessionAlert(req, OWNER_EDIT_EQUIPMENT_PAGE_ALERT);
        var resDto = (AddEditEquipmentResDto) httpSession.getAttribute(getClass().getName());
        if (isNull(resDto)) {
            try (final Session session = database.open()) {
                try {
                    session.beginTransaction();
                    final String jpqlFindEquipmentBaseId = "" +
                        "SELECT new pl.polsl.skirentalservice.dto.equipment.AddEditEquipmentReqDto(" +
                            "e.name, e.model, e.description, CAST(e.countInStore AS string), CAST(e.size AS string)," +
                            "CAST(e.pricePerHour AS string), CAST(e.priceForNextHour AS string)," +
                            "CAST(e.pricePerDay AS string), CAST(e.valueCost AS string), CAST(t.id AS string)," +
                            "CAST(b.id AS string), CAST(c.id AS string), e.gender" +
                        ") FROM EquipmentEntity e " +
                        "INNER JOIN e.equipmentType t INNER JOIN e.equipmentBrand b INNER JOIN e.equipmentColor c " +
                        "WHERE e.id = :eid";
                    final AddEditEquipmentReqDto equipmentDetails = session
                        .createQuery(jpqlFindEquipmentBaseId, AddEditEquipmentReqDto.class)
                        .setParameter("eid", equipmentId)
                        .getSingleResultOrNull();
                    if (isNull(equipmentDetails)) throw new EquipmentNotFoundException(equipmentId);

                    resDto = new AddEditEquipmentResDto(validator, equipmentDetails);

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
                    if (!isNull(session)) onHibernateException(session, LOGGER, ex);
                }
            } catch (RuntimeException ex) {
                alert.setMessage(ex.getMessage());
                httpSession.setAttribute(OWNER_EDIT_EQUIPMENT_PAGE_ALERT.getName(), alert);
            }
        }
        req.setAttribute("equipmentId", equipmentId);
        req.setAttribute("alertData", alert);
        req.setAttribute("addEditEquipmentData", resDto);
        req.setAttribute("addEditText", "Edytuj");
        req.setAttribute(EQ_TYPES_MODAL_DATA.getName(), getAndDestroySessionModalData(req, EQ_TYPES_MODAL_DATA));
        req.setAttribute(EQ_BRANDS_MODAL_DATA.getName(), getAndDestroySessionModalData(req, EQ_BRANDS_MODAL_DATA));
        req.setAttribute(EQ_COLORS_MODAL_DATA.getName(), getAndDestroySessionModalData(req, EQ_COLORS_MODAL_DATA));
        req.setAttribute("title", OWNER_EDIT_EQUIPMENT_PAGE.getName());
        req.getRequestDispatcher("/WEB-INF/pages/owner/equipment/owner-add-edit-equipment.jsp").forward(req, res);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final String equipmentId = req.getParameter("id");
        final AlertTupleDto alert = new AlertTupleDto(true);
        final HttpSession httpSession = req.getSession();
        final String loggedUser = getLoggedUserLogin(req);

        final AddEditEquipmentReqDto reqDto = new AddEditEquipmentReqDto(req);
        final AddEditEquipmentResDto resDto = new AddEditEquipmentResDto(validator, reqDto);
        if (validator.someFieldsAreInvalid(reqDto)) {
            httpSession.setAttribute(getClass().getName(), resDto);
            res.sendRedirect("/owner/edit-equipment?id=" + equipmentId);
            return;
        }
        try (final Session session = database.open()) {
            try {
                session.beginTransaction();

                final EquipmentEntity equipmentEntity = session.get(EquipmentEntity.class, equipmentId);
                if (isNull(equipmentEntity)) throw new EquipmentNotFoundException(equipmentId);

                final String jpqlFindEquipmentByModel =
                    "SELECT COUNT(e.id) > 0 FROM EquipmentEntity e WHERE LOWER(e.model) = LOWER(:model) AND e.id <> :eid";
                final Boolean modelAlreadyExist = session.createQuery(jpqlFindEquipmentByModel, Boolean.class)
                    .setParameter("model", reqDto.getModel())
                    .setParameter("eid", equipmentId)
                    .getSingleResult();
                if (modelAlreadyExist) throw new EquipmentAlreadyExistException();

                modelMapper.onUpdateNullableTransactTurnOn();
                modelMapper.shallowCopy(reqDto, equipmentEntity);
                modelMapper.onUpdateNullableTransactTurnOff();

                equipmentEntity.setEquipmentType(session.getReference(EquipmentTypeEntity.class, reqDto.getType()));
                equipmentEntity.setEquipmentBrand(session.getReference(EquipmentBrandEntity.class, reqDto.getBrand()));
                equipmentEntity.setEquipmentColor(session.getReference(EquipmentColorEntity.class, reqDto.getColor()));

                session.getTransaction().commit();
                alert.setType(INFO);
                alert.setMessage(
                    "Pomyślnie dokonano edycji istniejącego sprzętu narciarskiego z ID <strong>#" + equipmentId +
                    "</strong>."
                );
                httpSession.setAttribute(COMMON_EQUIPMENTS_PAGE_ALERT.getName(), alert);
                httpSession.removeAttribute(getClass().getName());
                LOGGER.info("Successful edit existing equipment with id: {} by: {}. Equipment data: {}",
                    equipmentId, loggedUser, reqDto);
                res.sendRedirect("/owner/equipments");
            } catch (RuntimeException ex) {
                onHibernateException(session, LOGGER, ex);
            }
        } catch (RuntimeException ex) {
            alert.setMessage(ex.getMessage());
            httpSession.setAttribute(getClass().getName(), resDto);
            httpSession.setAttribute(OWNER_EDIT_EQUIPMENT_PAGE_ALERT.getName(), alert);
            LOGGER.error("Unable to edit existing equipment with id: {}. Cause: {}", equipmentId, ex.getMessage());
            res.sendRedirect("/owner/edit-equipment?id=" + equipmentId);
        }
    }
}
