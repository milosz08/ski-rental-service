/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: EquipmentBrandDao.java
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

package pl.polsl.skirentalservice.dao.equipment_brand;

import lombok.RequiredArgsConstructor;

import org.hibernate.Session;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import pl.polsl.skirentalservice.dto.FormSelectTupleDto;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@RequiredArgsConstructor
public class EquipmentBrandDao implements IEquipmentBrandDao {

    private final Session session;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public List<FormSelectTupleDto> findAllEquipmentBrands() {
        final String jpqlFindEquipmentBrands = """
            SELECT new pl.polsl.skirentalservice.dto.FormSelectTupleDto(
                CAST(t.id AS string), t.name
            ) FROM EquipmentBrandEntity t ORDER BY t.id
        """;
        return session.createQuery(jpqlFindEquipmentBrands, FormSelectTupleDto.class)
            .getResultList();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Optional<String> getEquipmentBrandNameById(Object brandId) {
        final String jqplFindNameOfBrand= "SELECT b.name FROM EquipmentBrandEntity b WHERE b.id = :id";
        final String brandName = session.createQuery(jqplFindNameOfBrand, String.class)
            .setParameter("id", brandId)
            .getSingleResultOrNull();
        if (Objects.isNull(brandName)) return Optional.empty();
        return Optional.of(brandName);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public boolean checkIfEquipmentBrandExistByName(String brandName) {
        final String jpqlFindBrandAlreadyExist = """
            SELECT COUNT(b.id) > 0 FROM EquipmentBrandEntity b WHERE LOWER(b.name) = LOWER(:name)
        """;
        return session.createQuery(jpqlFindBrandAlreadyExist, Boolean.class)
            .setParameter("name", brandName)
            .getSingleResult();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public boolean checkIfEquipmentBrandHasAnyConnections(Object brandId) {
        final String jpqlFindBrandHasConnections = """
            SELECT COUNT(e.id) > 0 FROM EquipmentEntity e INNER JOIN e.equipmentBrand b WHERE b.id = :id
        """;
        return session.createQuery(jpqlFindBrandHasConnections, Boolean.class)
            .setParameter("id", brandId)
            .getSingleResult();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void deleteEquipmentBrandById(Object brandId) {
        session.createMutationQuery("DELETE EquipmentBrandEntity b WHERE b.id = :id")
            .setParameter("id", brandId).executeUpdate();
    }
}
