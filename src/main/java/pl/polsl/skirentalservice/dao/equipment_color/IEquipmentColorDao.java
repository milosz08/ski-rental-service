/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.dao.equipment_color;

import pl.polsl.skirentalservice.dto.FormSelectTupleDto;

import java.util.List;
import java.util.Optional;

public interface IEquipmentColorDao {
    List<FormSelectTupleDto> findAllEquipmentColors();
    Optional<String> getEquipmentColorNameById(Object colorId);

    boolean checkIfEquipmentColorExistByName(String colorName);
    boolean checkIfEquipmentColorHasAnyConnections(Object colorId);

    void deleteEquipmentColorById(Object colorId);
}
