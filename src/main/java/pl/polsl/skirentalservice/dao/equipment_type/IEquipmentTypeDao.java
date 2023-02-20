/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 * Silesian University of Technology
 *
 *  File name: IEquipmentTypeDto.java
 *  Last modified: 20/02/2023, 21:22
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.dao.equipment_type;

import java.util.*;
import pl.polsl.skirentalservice.dto.FormSelectTupleDto;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public interface IEquipmentTypeDao {
    List<FormSelectTupleDto> findAllEquipmentTypes();
    Optional<String> getEquipmentTypeNameById(Object typeId);

    boolean checkIfEquipmentTypeExistByName(String typeName);
    boolean checkIfEquipmentTypeHasAnyConnections(Object typeId);

    void deleteEquipmentTypeById(Object typeId);
}
