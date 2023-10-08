/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.dao.equipment_brand;

import pl.polsl.skirentalservice.dto.FormSelectTupleDto;

import java.util.List;
import java.util.Optional;

public interface IEquipmentBrandDao {
    List<FormSelectTupleDto> findAllEquipmentBrands();
    Optional<String> getEquipmentBrandNameById(Object brandId);

    boolean checkIfEquipmentBrandExistByName(String brandName);
    boolean checkIfEquipmentBrandHasAnyConnections(Object brandId);

    void deleteEquipmentBrandById(Object brandId);
}
