/*
 * Copyright (c) 2024 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.service.impl;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import pl.polsl.skirentalservice.core.db.PersistenceBean;
import pl.polsl.skirentalservice.dao.EquipmentDao;
import pl.polsl.skirentalservice.dao.hibernate.EquipmentDaoHib;
import pl.polsl.skirentalservice.dto.login.LoggedUserDataDto;
import pl.polsl.skirentalservice.dto.rent.AddEditEquipmentCartReqDto;
import pl.polsl.skirentalservice.dto.rent.AddEditEquipmentCartResDto;
import pl.polsl.skirentalservice.dto.rent.CartSingleEquipmentDataDto;
import pl.polsl.skirentalservice.dto.rent.InMemoryRentDataDto;
import pl.polsl.skirentalservice.exception.AlreadyExistException;
import pl.polsl.skirentalservice.exception.NotFoundException;
import pl.polsl.skirentalservice.service.CartEquipmentService;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Stateless
@SuppressWarnings("unused")
public class CartEquipmentServiceBean implements CartEquipmentService {
    private final PersistenceBean persistenceBean;

    @Inject
    public CartEquipmentServiceBean(PersistenceBean persistenceBean) {
        this.persistenceBean = persistenceBean;
    }

    @Override
    public void addEquipmentToCart(
        AddEditEquipmentCartReqDto reqDto, AddEditEquipmentCartResDto resDto,
        InMemoryRentDataDto rentData, LoggedUserDataDto loggedUser, Long equipmentId
    ) {
        persistenceBean.startNonTransactQuery(session -> {
            final EquipmentDao equipmentDao = new EquipmentDaoHib(session);
            final var eqDetails = equipmentDao
                .findEquipmentDetails(equipmentId)
                .orElseThrow(() -> new NotFoundException.EquipmentNotFoundException(equipmentId));

            if (rentData.getEquipments().stream().anyMatch(e -> e.getId().equals(eqDetails.getId()))) {
                throw new AlreadyExistException.EquipmentInCartAlreadyExistException();
            }
            if (eqDetails.getTotalCount() < Integer.parseInt(reqDto.getCount())) {
                throw new AlreadyExistException.TooMuchEquipmentsException();
            }
            final CartSingleEquipmentDataDto cartData = new CartSingleEquipmentDataDto(eqDetails, reqDto, resDto);
            rentData.getEquipments().add(cartData);

            log.info("Successfuly add equipment to memory-persist data container by: {}. Data: {}", loggedUser,
                cartData);
        });
    }

    @Override
    public void editEquipmentFromCart(
        AddEditEquipmentCartReqDto reqDto, AddEditEquipmentCartResDto resDto,
        InMemoryRentDataDto rentData, LoggedUserDataDto loggedUser, Long equipmentId
    ) {
        persistenceBean.startNonTransactQuery(session -> {
            final EquipmentDao equipmentDao = new EquipmentDaoHib(session);

            final var equipmentDetails = equipmentDao
                .findEquipmentDetails(equipmentId)
                .orElseThrow(() -> new NotFoundException.EquipmentNotFoundException(equipmentId));

            final CartSingleEquipmentDataDto cartData = rentData.getEquipments()
                .stream()
                .filter(e -> e.getId().equals(equipmentDetails.getId())).findFirst()
                .orElseThrow(NotFoundException.EquipmentInCartNotFoundException::new);

            if (equipmentDetails.getTotalCount() < Integer.parseInt(reqDto.getCount())) {
                throw new AlreadyExistException.TooMuchEquipmentsException();
            }
            cartData.setCount(reqDto.getCount());
            cartData.setDescription(reqDto.getDescription());

            if (!reqDto.getDepositPrice().isEmpty()) {
                cartData.getPriceUnits().setTotalDepositPriceNetto(new BigDecimal(reqDto.getDepositPrice()));
            }
            cartData.setResDto(resDto);
            log.info("Successfuly edit equipment from memory-persist data container by: {}. Data: {}", loggedUser,
                cartData);
        });
    }

    @Override
    public void deleteEquipmentFromCart(InMemoryRentDataDto rentData, Long equipmentId, LoggedUserDataDto loggedUser) {
        persistenceBean.startNonTransactQuery(session -> {
            final EquipmentDao equipmentDao = new EquipmentDaoHib(session);
            if (!equipmentDao.checkIfEquipmentExist(equipmentId)) {
                throw new NotFoundException.EquipmentNotFoundException(equipmentId);
            }
            final CartSingleEquipmentDataDto cartData = rentData.getEquipments().stream()
                .filter(e -> e.getId().equals(equipmentId))
                .findFirst()
                .orElseThrow(NotFoundException.EquipmentInCartNotFoundException::new);

            final List<CartSingleEquipmentDataDto> equipmentsWithoutSelected = rentData.getEquipments().stream()
                .filter(e -> !e.getId().equals(equipmentId))
                .collect(Collectors.toList());

            rentData.setEquipments(equipmentsWithoutSelected);
            log.info("Successfuly deleted equipment from memory-persist data container by: {}. Data: {}",
                loggedUser, cartData);
        });
    }

}
