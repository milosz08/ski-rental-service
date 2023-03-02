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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.modelmapper.ModelMapper;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Objects;
import java.io.IOException;

import pl.polsl.skirentalservice.util.*;
import pl.polsl.skirentalservice.core.ValidatorBean;
import pl.polsl.skirentalservice.core.db.HibernateUtil;
import pl.polsl.skirentalservice.core.ModelMapperGenerator;
import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.dto.equipment.AddEditEquipmentReqDto;
import pl.polsl.skirentalservice.dto.equipment.AddEditEquipmentResDto;
import pl.polsl.skirentalservice.dao.equipment.EquipmentDao;
import pl.polsl.skirentalservice.dao.equipment.IEquipmentDao;
import pl.polsl.skirentalservice.dao.equipment_brand.EquipmentBrandDao;
import pl.polsl.skirentalservice.dao.equipment_brand.IEquipmentBrandDao;
import pl.polsl.skirentalservice.dao.equipment_color.EquipmentColorDao;
import pl.polsl.skirentalservice.dao.equipment_color.IEquipmentColorDao;
import pl.polsl.skirentalservice.dao.equipment_type.EquipmentTypeDao;
import pl.polsl.skirentalservice.dao.equipment_type.IEquipmentTypeDao;
import pl.polsl.skirentalservice.entity.EquipmentEntity;
import pl.polsl.skirentalservice.entity.EquipmentTypeEntity;
import pl.polsl.skirentalservice.entity.EquipmentBrandEntity;
import pl.polsl.skirentalservice.entity.EquipmentColorEntity;

import static pl.polsl.skirentalservice.exception.NotFoundException.*;
import static pl.polsl.skirentalservice.exception.AlreadyExistException.*;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@WebServlet("/owner/edit-equipment")
public class OwnerEditEquipmentServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(OwnerEditEquipmentServlet.class);
    private final SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
    private final ModelMapper modelMapper = ModelMapperGenerator.getModelMapper();

    @EJB private ValidatorBean validator;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final String equipmentId = req.getParameter("id");
        final HttpSession httpSession = req.getSession();

        final AlertTupleDto alert = Utils.getAndDestroySessionAlert(req, SessionAlert.OWNER_EDIT_EQUIPMENT_PAGE_ALERT);
        var resDto = (AddEditEquipmentResDto) httpSession.getAttribute(getClass().getName());
        if (Objects.isNull(resDto)) {
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
                    if (!Objects.isNull(session)) Utils.onHibernateException(session, LOGGER, ex);
                }
            } catch (RuntimeException ex) {
                alert.setMessage(ex.getMessage());
                httpSession.setAttribute(SessionAlert.OWNER_EDIT_EQUIPMENT_PAGE_ALERT.getName(), alert);
            }
        }
        req.setAttribute("equipmentId", equipmentId);
        req.setAttribute("alertData", alert);
        req.setAttribute("addEditEquipmentData", resDto);
        req.setAttribute("addEditText", "Edytuj");
        req.setAttribute(SessionAttribute.EQ_TYPES_MODAL_DATA.getName(),
            Utils.getAndDestroySessionModalData(req, SessionAttribute.EQ_TYPES_MODAL_DATA));
        req.setAttribute(SessionAttribute.EQ_BRANDS_MODAL_DATA.getName(),
            Utils.getAndDestroySessionModalData(req, SessionAttribute.EQ_BRANDS_MODAL_DATA));
        req.setAttribute(SessionAttribute.EQ_COLORS_MODAL_DATA.getName(),
            Utils.getAndDestroySessionModalData(req, SessionAttribute.EQ_COLORS_MODAL_DATA));
        req.setAttribute("title", PageTitle.OWNER_EDIT_EQUIPMENT_PAGE.getName());
        req.getRequestDispatcher("/WEB-INF/pages/owner/equipment/owner-add-edit-equipment.jsp").forward(req, res);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final String equipmentId = req.getParameter("id");
        final AlertTupleDto alert = new AlertTupleDto(true);
        final HttpSession httpSession = req.getSession();
        final String loggedUser = Utils.getLoggedUserLogin(req);

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
                if (Objects.isNull(equipmentEntity)) throw new EquipmentNotFoundException(equipmentId);

                if (equipmentDao.checkIfEquipmentModelExist(reqDto.getModel(), equipmentId)) {
                    throw new EquipmentAlreadyExistException();
                }
                ModelMapperGenerator.onUpdateNullableTransactTurnOn();
                modelMapper.map(reqDto, equipmentEntity);
                ModelMapperGenerator.onUpdateNullableTransactTurnOff();

                equipmentEntity.setEquipmentType(session.getReference(EquipmentTypeEntity.class, reqDto.getType()));
                equipmentEntity.setEquipmentBrand(session.getReference(EquipmentBrandEntity.class, reqDto.getBrand()));
                equipmentEntity.setEquipmentColor(session.getReference(EquipmentColorEntity.class, reqDto.getColor()));

                session.getTransaction().commit();
                alert.setType(AlertType.INFO);
                alert.setMessage(
                    "Pomyślnie dokonano edycji istniejącego sprzętu narciarskiego z ID <strong>#" + equipmentId +
                    "</strong>."
                );
                httpSession.setAttribute(SessionAlert.COMMON_EQUIPMENTS_PAGE_ALERT.getName(), alert);
                httpSession.removeAttribute(getClass().getName());
                LOGGER.info("Successful edit existing equipment with id: {} by: {}. Equipment data: {}",
                    equipmentId, loggedUser, reqDto);
                res.sendRedirect("/owner/equipments");
            } catch (RuntimeException ex) {
                Utils.onHibernateException(session, LOGGER, ex);
            }
        } catch (RuntimeException ex) {
            alert.setMessage(ex.getMessage());
            httpSession.setAttribute(getClass().getName(), resDto);
            httpSession.setAttribute(SessionAlert.OWNER_EDIT_EQUIPMENT_PAGE_ALERT.getName(), alert);
            LOGGER.error("Unable to edit existing equipment with id: {}. Cause: {}", equipmentId, ex.getMessage());
            res.sendRedirect("/owner/edit-equipment?id=" + equipmentId);
        }
    }
}
