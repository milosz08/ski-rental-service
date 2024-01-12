/*
 * Copyright (c) 2024 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.service;

import jakarta.ejb.Local;
import pl.polsl.skirentalservice.core.servlet.pageable.Slice;
import pl.polsl.skirentalservice.dto.PageableDto;
import pl.polsl.skirentalservice.dto.equipment.AddEditEquipmentReqDto;
import pl.polsl.skirentalservice.dto.equipment.EquipmentDetailsResDto;
import pl.polsl.skirentalservice.dto.equipment.EquipmentRecordResDto;
import pl.polsl.skirentalservice.dto.login.LoggedUserDataDto;

@Local
public interface EquipmentService {
    Slice<EquipmentRecordResDto> getPageableEquipments(PageableDto pageableDto);
    AddEditEquipmentReqDto getEquipmentDetails(Long equipmentId);
    EquipmentDetailsResDto getFullEquipmentDetails(Long equipmentId);
    boolean checkIfEquipmentExist(Long equipmentId);
    void createNewEquipment(AddEditEquipmentReqDto reqDto, LoggedUserDataDto loggedUser);
    void editEquipment(Long equipmentId, AddEditEquipmentReqDto reqDto, LoggedUserDataDto loggedUser);
    void deleteEquipment(Long equipmentId, LoggedUserDataDto loggedUser);
}
