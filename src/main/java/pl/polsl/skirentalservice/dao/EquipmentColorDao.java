package pl.polsl.skirentalservice.dao;

import pl.polsl.skirentalservice.dto.FormSelectTupleDto;

import java.util.List;
import java.util.Optional;

public interface EquipmentColorDao {
    List<FormSelectTupleDto> findAllEquipmentColors();

    Optional<String> getEquipmentColorNameById(Object colorId);

    boolean checkIfEquipmentColorExistByName(String colorName);

    boolean checkIfEquipmentColorHasAnyConnections(Object colorId);

    void deleteEquipmentColorById(Object colorId);
}
