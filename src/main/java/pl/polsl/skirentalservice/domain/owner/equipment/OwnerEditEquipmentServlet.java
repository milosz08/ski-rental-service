/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 * Silesian University of Technology
 *
 *  File name: OwnerEditEquipmentServlet.java
 *  Last modified: 08/02/2023, 22:09
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.domain.owner.equipment;

import org.slf4j.*;
import org.hibernate.*;
import org.modelmapper.ModelMapper;

import jakarta.ejb.EJB;
import jakarta.servlet.http.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;

import pl.polsl.skirentalservice.core.*;
import pl.polsl.skirentalservice.entity.*;
import pl.polsl.skirentalservice.dto.equipment.*;
import pl.polsl.skirentalservice.dao.equipment.*;
import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.dao.equipment_type.*;
import pl.polsl.skirentalservice.dao.equipment_brand.*;
import pl.polsl.skirentalservice.dao.equipment_color.*;

import java.io.IOException;

import static java.util.Objects.isNull;

import static pl.polsl.skirentalservice.util.Utils.*;
import static pl.polsl.skirentalservice.util.SessionAlert.*;
import static pl.polsl.skirentalservice.util.AlertType.INFO;
import static pl.polsl.skirentalservice.util.SessionAttribute.*;
import static pl.polsl.skirentalservice.core.ModelMapperGenerator.*;
import static pl.polsl.skirentalservice.exception.NotFoundException.*;
import static pl.polsl.skirentalservice.exception.AlreadyExistException.*;
import static pl.polsl.skirentalservice.core.db.HibernateUtil.getSessionFactory;
import static pl.polsl.skirentalservice.util.PageTitle.OWNER_EDIT_EQUIPMENT_PAGE;
import static pl.polsl.skirentalservice.util.SessionAttribute.EQ_COLORS_MODAL_DATA;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@WebServlet("/owner/edit-equipment")
public class OwnerEditEquipmentServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(OwnerEditEquipmentServlet.class);
    private final SessionFactory sessionFactory = getSessionFactory();
    private final ModelMapper modelMapper = getModelMapper();

    @EJB private ValidatorBean validator;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final String equipmentId = req.getParameter("id");
        final HttpSession httpSession = req.getSession();

        final AlertTupleDto alert = getAndDestroySessionAlert(req, OWNER_EDIT_EQUIPMENT_PAGE_ALERT);
        var resDto = (AddEditEquipmentResDto) httpSession.getAttribute(getClass().getName());
        if (isNull(resDto)) {
            try (final Session session = sessionFactory.openSession()) {
                try {
                    session.beginTransaction();

                    final IEquipmentDao equipmentDao = new EquipmentDao(session);
                    final IEquipmentTypeDao equipmentTypeDao = new EquipmentTypeDao(session);
                    final IEquipmentBrandDao equipmentBrandDao = new EquipmentBrandDao(session);
                    final IEquipmentColorDao equipmentColorDao = new EquipmentColorDao(session);

                    final var equipmentDetails = equipmentDao.findAddEditEquipmentDetails(equipmentId)
                        .orElseThrow(() -> { throw new EquipmentNotFoundException(equipmentId); });

                    resDto = new AddEditEquipmentResDto(validator, equipmentDetails);
                    resDto.insertTypesSelects(equipmentTypeDao.findAllEquipmentTypes());
                    resDto.insertBrandsSelects(equipmentBrandDao.findAllEquipmentBrands());
                    resDto.insertColorsSelects(equipmentColorDao.findAllEquipmentColors());

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
        try (final Session session = sessionFactory.openSession()) {
            try {
                session.beginTransaction();
                final IEquipmentDao equipmentDao = new EquipmentDao(session);

                final EquipmentEntity equipmentEntity = session.get(EquipmentEntity.class, equipmentId);
                if (isNull(equipmentEntity)) throw new EquipmentNotFoundException(equipmentId);

                if (equipmentDao.checkIfEquipmentModelExist(reqDto.getModel(), equipmentId)) {
                    throw new EquipmentAlreadyExistException();
                }
                onUpdateNullableTransactTurnOn();
                modelMapper.map(reqDto, equipmentEntity);
                onUpdateNullableTransactTurnOff();

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
