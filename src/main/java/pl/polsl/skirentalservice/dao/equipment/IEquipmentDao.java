/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.dao.equipment;

import pl.polsl.skirentalservice.dto.PageableDto;
import pl.polsl.skirentalservice.dto.deliv_return.RentReturnEquipmentRecordResDto;
import pl.polsl.skirentalservice.dto.equipment.AddEditEquipmentReqDto;
import pl.polsl.skirentalservice.dto.equipment.EquipmentDetailsResDto;
import pl.polsl.skirentalservice.dto.equipment.EquipmentRecordResDto;
import pl.polsl.skirentalservice.dto.rent.EquipmentRentRecordResDto;
import pl.polsl.skirentalservice.dto.rent.RentEquipmentsDetailsResDto;
import pl.polsl.skirentalservice.paging.filter.FilterDataDto;

import java.util.List;
import java.util.Optional;

public interface IEquipmentDao {
    void decreaseAvailableSelectedEquipmentCount(Object equipmentId, Object count);
    void increaseAvailableSelectedEquipmentCount(Object equipmentId, Object count);

    Optional<EquipmentRentRecordResDto> findEquipmentDetails(Object equipmentId);
    Optional<AddEditEquipmentReqDto> findAddEditEquipmentDetails(Object equipmentId);
    Optional<EquipmentDetailsResDto> findEquipmentDetailsPage(Object equipmentId);

    boolean checkIfEquipmentExist(Object equipmentId);
    boolean checkIfEquipmentModelExist(String modelName, Object equipmentId);
    boolean checkIfBarCodeExist(String barcode);
    boolean checkIfEquipmentHasOpenedRents(Object equipmentId);

    List<RentReturnEquipmentRecordResDto> findAllEquipmentsConnectedWithRentReturn(Object rentId);
    List<RentEquipmentsDetailsResDto> findAllEquipmentsConnectedWithReturn(Object returnId);
    List<RentEquipmentsDetailsResDto> findAllEquipmentsConnectedWithRent(Object rentId);

    Long getCountIfSomeEquipmentsAreAvailable();
    Long findAllEquipmentsCount(FilterDataDto filterData);
    Integer findAllEquipmentsInCartCount(Object equipmentId);

    List<EquipmentRecordResDto> findAllPageableEquipmentRecords(PageableDto pageableDto);
    List<EquipmentRentRecordResDto> findAllPageableEquipments(PageableDto pageableDto);
}
