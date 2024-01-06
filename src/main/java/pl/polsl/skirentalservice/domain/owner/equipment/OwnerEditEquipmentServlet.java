/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.domain.owner.equipment;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.modelmapper.ModelMapper;
import pl.polsl.skirentalservice.core.ModelMapperGenerator;
import pl.polsl.skirentalservice.core.ValidatorSingleton;
import pl.polsl.skirentalservice.core.db.HibernateDbSingleton;
import pl.polsl.skirentalservice.dao.EquipmentBrandDao;
import pl.polsl.skirentalservice.dao.EquipmentColorDao;
import pl.polsl.skirentalservice.dao.EquipmentDao;
import pl.polsl.skirentalservice.dao.EquipmentTypeDao;
import pl.polsl.skirentalservice.dao.hibernate.EquipmentBrandDaoHib;
import pl.polsl.skirentalservice.dao.hibernate.EquipmentColorDaoHib;
import pl.polsl.skirentalservice.dao.hibernate.EquipmentDaoHib;
import pl.polsl.skirentalservice.dao.hibernate.EquipmentTypeDaoHib;
import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.dto.equipment.AddEditEquipmentReqDto;
import pl.polsl.skirentalservice.dto.equipment.AddEditEquipmentResDto;
import pl.polsl.skirentalservice.entity.EquipmentBrandEntity;
import pl.polsl.skirentalservice.entity.EquipmentColorEntity;
import pl.polsl.skirentalservice.entity.EquipmentEntity;
import pl.polsl.skirentalservice.entity.EquipmentTypeEntity;
import pl.polsl.skirentalservice.util.*;

import java.io.IOException;

import static pl.polsl.skirentalservice.exception.AlreadyExistException.EquipmentAlreadyExistException;
import static pl.polsl.skirentalservice.exception.NotFoundException.EquipmentNotFoundException;

@Slf4j
@WebServlet("/owner/edit-equipment")
public class OwnerEditEquipmentServlet extends HttpServlet {
    private final SessionFactory sessionFactory = HibernateDbSingleton.getInstance().getSessionFactory();
    private final ValidatorSingleton validator = ValidatorSingleton.getInstance();

    private final ModelMapper modelMapper = ModelMapperGenerator.getModelMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final String equipmentId = req.getParameter("id");
        final HttpSession httpSession = req.getSession();

        final AlertTupleDto alert = Utils.getAndDestroySessionAlert(req, SessionAlert.OWNER_EDIT_EQUIPMENT_PAGE_ALERT);
        var resDto = (AddEditEquipmentResDto) httpSession.getAttribute(getClass().getName());
        if (resDto == null) {
            try (final Session session = sessionFactory.openSession()) {
                try {
                    session.beginTransaction();

                    final EquipmentDao equipmentDao = new EquipmentDaoHib(session);
                    final EquipmentTypeDao equipmentTypeDao = new EquipmentTypeDaoHib(session);
                    final EquipmentBrandDao equipmentBrandDao = new EquipmentBrandDaoHib(session);
                    final EquipmentColorDao equipmentColorDao = new EquipmentColorDaoHib(session);

                    final var equipmentDetails = equipmentDao.findAddEditEquipmentDetails(equipmentId)
                        .orElseThrow(() -> new EquipmentNotFoundException(equipmentId));

                    resDto = new AddEditEquipmentResDto(validator, equipmentDetails);
                    resDto.insertTypesSelects(equipmentTypeDao.findAllEquipmentTypes());
                    resDto.insertBrandsSelects(equipmentBrandDao.findAllEquipmentBrands());
                    resDto.insertColorsSelects(equipmentColorDao.findAllEquipmentColors());

                    session.getTransaction().commit();
                } catch (RuntimeException ex) {
                    Utils.onHibernateException(session, log, ex);
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
                final EquipmentDao equipmentDao = new EquipmentDaoHib(session);

                final EquipmentEntity equipmentEntity = session.get(EquipmentEntity.class, equipmentId);
                if (equipmentEntity == null) {
                    throw new EquipmentNotFoundException(equipmentId);
                }
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
                log.info("Successful edit existing equipment with id: {} by: {}. Equipment data: {}",
                    equipmentId, loggedUser, reqDto);
                res.sendRedirect("/owner/equipments");
            } catch (RuntimeException ex) {
                Utils.onHibernateException(session, log, ex);
            }
        } catch (RuntimeException ex) {
            alert.setMessage(ex.getMessage());
            httpSession.setAttribute(getClass().getName(), resDto);
            httpSession.setAttribute(SessionAlert.OWNER_EDIT_EQUIPMENT_PAGE_ALERT.getName(), alert);
            log.error("Unable to edit existing equipment with id: {}. Cause: {}", equipmentId, ex.getMessage());
            res.sendRedirect("/owner/edit-equipment?id=" + equipmentId);
        }
    }
}
