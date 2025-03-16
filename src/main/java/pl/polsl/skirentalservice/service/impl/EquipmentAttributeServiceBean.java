package pl.polsl.skirentalservice.service.impl;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import pl.polsl.skirentalservice.core.ValidatorBean;
import pl.polsl.skirentalservice.core.db.PersistenceBean;
import pl.polsl.skirentalservice.core.servlet.WebServletRequest;
import pl.polsl.skirentalservice.dao.EquipmentBrandDao;
import pl.polsl.skirentalservice.dao.EquipmentColorDao;
import pl.polsl.skirentalservice.dao.EquipmentTypeDao;
import pl.polsl.skirentalservice.dao.hibernate.EquipmentBrandDaoHib;
import pl.polsl.skirentalservice.dao.hibernate.EquipmentColorDaoHib;
import pl.polsl.skirentalservice.dao.hibernate.EquipmentTypeDaoHib;
import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.dto.FormSelectTupleDto;
import pl.polsl.skirentalservice.dto.attribute.AttributeModalReqDto;
import pl.polsl.skirentalservice.dto.attribute.AttributeModalResDto;
import pl.polsl.skirentalservice.dto.attribute.AttributeValidatorPayloadDto;
import pl.polsl.skirentalservice.dto.login.LoggedUserDataDto;
import pl.polsl.skirentalservice.entity.EquipmentBrandEntity;
import pl.polsl.skirentalservice.entity.EquipmentColorEntity;
import pl.polsl.skirentalservice.entity.EquipmentTypeEntity;
import pl.polsl.skirentalservice.exception.AlreadyExistException;
import pl.polsl.skirentalservice.exception.NotFoundException;
import pl.polsl.skirentalservice.service.EquipmentAttributeService;
import pl.polsl.skirentalservice.util.SessionAttribute;

import java.util.List;
import java.util.Map;

@Slf4j
@Stateless
@SuppressWarnings("unused")
public class EquipmentAttributeServiceBean implements EquipmentAttributeService {
    private final PersistenceBean persistenceBean;
    private final ValidatorBean validatorBean;

    @Inject
    public EquipmentAttributeServiceBean(PersistenceBean persistenceBean, ValidatorBean validatorBean) {
        this.persistenceBean = persistenceBean;
        this.validatorBean = validatorBean;
    }

    @Override
    public Map<SessionAttribute, List<FormSelectTupleDto>> getMergedEquipmentAttributes() {
        return persistenceBean.startNonTransactQuery(session -> {
            final EquipmentTypeDao equipmentTypeDao = new EquipmentTypeDaoHib(session);
            final EquipmentBrandDao equipmentBrandDao = new EquipmentBrandDaoHib(session);
            final EquipmentColorDao equipmentColorDao = new EquipmentColorDaoHib(session);
            return Map.of(
                SessionAttribute.EQ_TYPES_MODAL_DATA, equipmentTypeDao.findAllEquipmentTypes(),
                SessionAttribute.EQ_BRANDS_MODAL_DATA, equipmentBrandDao.findAllEquipmentBrands(),
                SessionAttribute.EQ_COLORS_MODAL_DATA, equipmentColorDao.findAllEquipmentColors()
            );
        });
    }

    @Override
    public void createNewEquipmentBrand(String brandName, LoggedUserDataDto loggedUser) {
        persistenceBean.startTransaction(session -> {
            final EquipmentBrandDao equipmentDetailsDao = new EquipmentBrandDaoHib(session);
            if (equipmentDetailsDao.checkIfEquipmentBrandExistByName(brandName)) {
                throw new AlreadyExistException.EquipmentBrandAlreadyExistException();
            }
            final EquipmentBrandEntity brandEntity = new EquipmentBrandEntity(brandName);
            session.persist(brandEntity);
            session.getTransaction().commit();
            log.info("Successful added new equipment brand by: {}. Brand: {}", loggedUser.getLogin(), brandName);
        });
    }

    @Override
    public void createNewEquipmentColor(String colorName, LoggedUserDataDto loggedUser) {
        persistenceBean.startTransaction(session -> {
            final EquipmentColorDao equipmentDetailsDao = new EquipmentColorDaoHib(session);
            if (equipmentDetailsDao.checkIfEquipmentColorExistByName(colorName)) {
                throw new AlreadyExistException.EquipmentColorAlreadyExistException();
            }
            final EquipmentColorEntity colorEntity = new EquipmentColorEntity(colorName);
            session.persist(colorEntity);
            session.getTransaction().commit();
            log.info("Successful added new equipment color by: {}. Color: {}", loggedUser.getLogin(), colorName);
        });
    }

    @Override
    public void createNewEquipmentType(String typeName, LoggedUserDataDto loggedUser) {
        persistenceBean.startTransaction(session -> {
            final EquipmentTypeDao equipmentDetailsDao = new EquipmentTypeDaoHib(session);

            if (equipmentDetailsDao.checkIfEquipmentTypeExistByName(typeName)) {
                throw new AlreadyExistException.EquipmentTypeAlreadyExistException();
            }
            final EquipmentTypeEntity typeEntity = new EquipmentTypeEntity(typeName);
            session.persist(typeEntity);
            session.getTransaction().commit();
            log.info("Successful added new equipment type by: {}. Type: {}", loggedUser.getLogin(), typeName);
        });
    }

    @Override
    public String deleteEquipmentBrand(Object brandId, LoggedUserDataDto loggedUser) {
        return persistenceBean.startTransaction(session -> {
            final EquipmentBrandDao equipmentDetailsDao = new EquipmentBrandDaoHib(session);

            final String deletedBrand = equipmentDetailsDao
                .getEquipmentBrandNameById(brandId)
                .orElseThrow(NotFoundException.EquipmentBrandNotFoundException::new);

            if (equipmentDetailsDao.checkIfEquipmentBrandHasAnyConnections(brandId)) {
                throw new AlreadyExistException.EquipmentBrandHasConnectionsException();
            }
            equipmentDetailsDao.deleteEquipmentBrandById(brandId);
            session.getTransaction().commit();

            log.info("Successful deleted equipment brand by: {}. Brand: {}", loggedUser.getLogin(), deletedBrand);
            return deletedBrand;
        });
    }

    @Override
    public String deleteEquipmentColor(Object colorId, LoggedUserDataDto loggedUser) {
        return persistenceBean.startTransaction(session -> {
            final EquipmentColorDao equipmentDetailsDao = new EquipmentColorDaoHib(session);

            final String deletedColor = equipmentDetailsDao
                .getEquipmentColorNameById(colorId)
                .orElseThrow(NotFoundException.EquipmentColorNotFoundException::new);

            if (equipmentDetailsDao.checkIfEquipmentColorHasAnyConnections(colorId)) {
                throw new AlreadyExistException.EquipmentColorHasConnectionsException();
            }
            equipmentDetailsDao.deleteEquipmentColorById(colorId);
            session.getTransaction().commit();

            log.info("Successful deleted equipment color by: {}. Color: {}", loggedUser.getLogin(), deletedColor);
            return deletedColor;
        });
    }

    @Override
    public String deleteEquipmentType(Object typeId, LoggedUserDataDto loggedUser) {
        return persistenceBean.startTransaction(session -> {
            final EquipmentTypeDao equipmentDetailsDao = new EquipmentTypeDaoHib(session);

            final String deletedType = equipmentDetailsDao
                .getEquipmentTypeNameById(typeId)
                .orElseThrow(NotFoundException.EquipmentTypeNotFoundException::new);

            if (equipmentDetailsDao.checkIfEquipmentTypeHasAnyConnections(typeId)) {
                throw new AlreadyExistException.EquipmentTypeHasConnectionsException();
            }
            equipmentDetailsDao.deleteEquipmentTypeById(typeId);
            session.getTransaction().commit();

            log.info("Successful deleted equipment type by: {}. Type: {}", loggedUser, deletedType);
            return deletedType;
        });
    }

    @Override
    public AttributeValidatorPayloadDto validateEquipmentAttribute(WebServletRequest req) {
        final AlertTupleDto alert = new AlertTupleDto();
        final AttributeModalReqDto reqDto = new AttributeModalReqDto(req);
        final AttributeModalResDto resDto = new AttributeModalResDto(validatorBean, reqDto, alert);

        resDto.setModalImmediatelyOpen(validatorBean.someFieldsAreInvalid(reqDto));
        return AttributeValidatorPayloadDto.builder()
            .alert(alert)
            .reqDto(reqDto)
            .resDto(resDto)
            .isInvalid(validatorBean.someFieldsAreInvalid(reqDto))
            .build();
    }
}
