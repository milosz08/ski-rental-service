/*
 * Copyright (c) 2024 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.service;

import jakarta.ejb.Local;
import pl.polsl.skirentalservice.core.servlet.WebServletRequest;
import pl.polsl.skirentalservice.dto.FormSelectTupleDto;
import pl.polsl.skirentalservice.dto.attribute.AttributeValidatorPayloadDto;
import pl.polsl.skirentalservice.dto.login.LoggedUserDataDto;
import pl.polsl.skirentalservice.util.SessionAttribute;

import java.util.List;
import java.util.Map;

@Local
public interface EquipmentAttributeService {
    Map<SessionAttribute, List<FormSelectTupleDto>> getMergedEquipmentAttributes();
    void createNewEquipmentBrand(String brandName, LoggedUserDataDto loggedUser);
    void createNewEquipmentColor(String colorName, LoggedUserDataDto loggedUser);
    void createNewEquipmentType(String typeName, LoggedUserDataDto loggedUser);
    String deleteEquipmentBrand(Object brandId, LoggedUserDataDto loggedUser);
    String deleteEquipmentColor(Object colorId, LoggedUserDataDto loggedUser);
    String deleteEquipmentType(Object typeId, LoggedUserDataDto loggedUser);
    AttributeValidatorPayloadDto validateEquipmentAttribute(WebServletRequest req);
}
