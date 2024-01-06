/*
 * Copyright (c) 2024 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.dao;

import pl.polsl.skirentalservice.dto.FormSelectTupleDto;

import java.util.List;
import java.util.Optional;

public interface EquipmentTypeDao {
    List<FormSelectTupleDto> findAllEquipmentTypes();
    Optional<String> getEquipmentTypeNameById(Object typeId);
    boolean checkIfEquipmentTypeExistByName(String typeName);
    boolean checkIfEquipmentTypeHasAnyConnections(Object typeId);
    void deleteEquipmentTypeById(Object typeId);
}
