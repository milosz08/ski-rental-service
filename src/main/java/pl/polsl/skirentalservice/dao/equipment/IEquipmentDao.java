/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: IEquipmentDao.java
 * Last modified: 6/3/23, 1:24 AM
 * Project name: ski-rental-service
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 *
 *     <http://www.apache.org/license/LICENSE-2.0>
 *
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the license.
 */

package pl.polsl.skirentalservice.dao.equipment;

import java.util.List;
import java.util.Optional;

import pl.polsl.skirentalservice.dto.PageableDto;
import pl.polsl.skirentalservice.paging.filter.FilterDataDto;
import pl.polsl.skirentalservice.dto.equipment.EquipmentRecordResDto;
import pl.polsl.skirentalservice.dto.equipment.EquipmentDetailsResDto;
import pl.polsl.skirentalservice.dto.equipment.AddEditEquipmentReqDto;
import pl.polsl.skirentalservice.dto.rent.EquipmentRentRecordResDto;
import pl.polsl.skirentalservice.dto.rent.RentEquipmentsDetailsResDto;
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

    Long getCountIfSomeEquipmentsAreAvailable();
    Long findAllEquipmentsCount(FilterDataDto filterData);
    Integer findAllEquipmentsInCartCount(Object equipmentId);

    List<EquipmentRecordResDto> findAllPageableEquipmentRecords(PageableDto pageableDto);
    List<EquipmentRentRecordResDto> findAllPageableEquipments(PageableDto pageableDto);
}
