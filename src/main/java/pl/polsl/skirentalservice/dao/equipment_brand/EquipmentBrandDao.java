/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 * Silesian University of Technology
 *
 *  File name: EquipmentBrandDto.java
 *  Last modified: 20/02/2023, 21:21
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.dao.equipment_brand;

import lombok.RequiredArgsConstructor;

import java.util.*;
import org.hibernate.Session;

import pl.polsl.skirentalservice.dto.FormSelectTupleDto;

import static java.util.Optional.*;
import static java.util.Objects.isNull;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@RequiredArgsConstructor
public class EquipmentBrandDao implements IEquipmentBrandDao {

    private final Session session;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public List<FormSelectTupleDto> findAllEquipmentBrands() {
        final String jpqlFindEquipmentBrands =
            "SELECT new pl.polsl.skirentalservice.dto.FormSelectTupleDto(" +
                "CAST(t.id AS string), t.name" +
            ") FROM EquipmentBrandEntity t ORDER BY t.id";
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
        if (isNull(brandName)) return empty();
        return of(brandName);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public boolean checkIfEquipmentBrandExistByName(String brandName) {
        final String jpqlFindBrandAlreadyExist =
            "SELECT COUNT(b.id) > 0 FROM EquipmentBrandEntity b WHERE LOWER(b.name) = LOWER(:name)";
        return session.createQuery(jpqlFindBrandAlreadyExist, Boolean.class)
            .setParameter("name", brandName)
            .getSingleResult();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public boolean checkIfEquipmentBrandHasAnyConnections(Object brandId) {
        final String jpqlFindBrandHasConnections =
            "SELECT COUNT(e.id) > 0 FROM EquipmentEntity e INNER JOIN e.equipmentBrand b WHERE b.id = :id";
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
