/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 * Silesian University of Technology
 *
 *  File name: EquipmentDao.java
 *  Last modified: 20/02/2023, 18:56
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.dao.equipment;

import java.util.*;

import pl.polsl.skirentalservice.dto.rent.*;
import pl.polsl.skirentalservice.dto.equipment.*;
import pl.polsl.skirentalservice.paging.filter.FilterDataDto;
import pl.polsl.skirentalservice.paging.sorter.SorterDataDto;
import pl.polsl.skirentalservice.dto.deliv_return.RentReturnEquipmentRecordResDto;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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

    Long findAllEquipmentsCount(FilterDataDto filterData);
    Integer findAllEquipmentsInCartCount(Object equipmentId);

    List<EquipmentRecordResDto> findAllPageableEquipmentRecords(
        FilterDataDto filterData, SorterDataDto sorterData, int page, int total);
    List<EquipmentRentRecordResDto> findAllPageableEquipments(
        FilterDataDto filterData, SorterDataDto sorterData, int page, int total);
}