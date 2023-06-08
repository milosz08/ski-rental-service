/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: EquipmentColorDao.java
 * Last modified: 3/12/23, 11:01 AM
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

package pl.polsl.skirentalservice.dao.equipment_color;

import lombok.RequiredArgsConstructor;

import org.hibernate.Session;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import pl.polsl.skirentalservice.dto.FormSelectTupleDto;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@RequiredArgsConstructor
public class EquipmentColorDao implements IEquipmentColorDao {

    private final Session session;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public List<FormSelectTupleDto> findAllEquipmentColors() {
        final String jpqlFindEquipmentColors = """
            SELECT new pl.polsl.skirentalservice.dto.FormSelectTupleDto(
                CAST(t.id AS string), t.name
            ) FROM EquipmentColorEntity t ORDER BY t.id
        """;
        return session.createQuery(jpqlFindEquipmentColors, FormSelectTupleDto.class)
            .getResultList();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Optional<String> getEquipmentColorNameById(Object colorId) {
        final String jqplFindNameOfColor = "SELECT c.name FROM EquipmentColorEntity c WHERE c.id = :id";
        final String colorName = session.createQuery(jqplFindNameOfColor, String.class)
            .setParameter("id", colorId)
            .getSingleResultOrNull();
        if (Objects.isNull(colorName)) return Optional.empty();
        return Optional.of(colorName);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public boolean checkIfEquipmentColorExistByName(String colorName) {
        final String jpqlFindColorAlreadyExist = """
            SELECT COUNT(c.id) > 0 FROM EquipmentColorEntity c WHERE LOWER(c.name) = LOWER(:name)
        """;
        return session.createQuery(jpqlFindColorAlreadyExist, Boolean.class)
            .setParameter("name", colorName)
            .getSingleResult();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public boolean checkIfEquipmentColorHasAnyConnections(Object colorId) {
        final String jpqlFindColorHasConnections = """
            SELECT COUNT(e.id) > 0 FROM EquipmentEntity e INNER JOIN e.equipmentColor c WHERE c.id = :id
        """;
        return session.createQuery(jpqlFindColorHasConnections, Boolean.class)
            .setParameter("id", colorId)
            .getSingleResult();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void deleteEquipmentColorById(Object colorId) {
        session.createMutationQuery("DELETE EquipmentColorEntity c WHERE c.id = :id")
            .setParameter("id", colorId).executeUpdate();
    }
}
