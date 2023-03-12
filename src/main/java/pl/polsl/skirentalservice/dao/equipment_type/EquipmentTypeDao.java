/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 * Silesian University of Technology
 *
 *  File name: EquipmentTypeDto.java
 *  Last modified: 20/02/2023, 21:22
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.dao.equipment_type;

import lombok.RequiredArgsConstructor;

import org.hibernate.Session;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import pl.polsl.skirentalservice.dto.FormSelectTupleDto;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@RequiredArgsConstructor
public class EquipmentTypeDao implements IEquipmentTypeDao {

    private final Session session;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public List<FormSelectTupleDto> findAllEquipmentTypes() {
        final String jpqlFindEquipmentTypes = """
            SELECT new pl.polsl.skirentalservice.dto.FormSelectTupleDto(
                CAST(t.id AS string), t.name
            ) FROM EquipmentTypeEntity t ORDER BY t.id
        """;
        return session.createQuery(jpqlFindEquipmentTypes, FormSelectTupleDto.class)
            .getResultList();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Optional<String> getEquipmentTypeNameById(Object typeId) {
        final String jqplFindNameOfType = "SELECT t.name FROM EquipmentTypeEntity t WHERE t.id = :id";
        final String typeName = session.createQuery(jqplFindNameOfType, String.class)
            .setParameter("id", typeId)
            .getSingleResultOrNull();
        if (Objects.isNull(typeName)) return Optional.empty();
        return Optional.of(typeName);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public boolean checkIfEquipmentTypeExistByName(String typeName) {
        final String jpqlFindTypeAlreadyExist = """
            SELECT COUNT(t.id) > 0 FROM EquipmentTypeEntity t WHERE LOWER(t.name) = LOWER(:name)
        """;
        return session.createQuery(jpqlFindTypeAlreadyExist, Boolean.class)
            .setParameter("name", typeName)
            .getSingleResult();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public boolean checkIfEquipmentTypeHasAnyConnections(Object typeId) {
        final String jpqlFindTypeHasConnections = """
            SELECT COUNT(e.id) > 0 FROM EquipmentEntity e INNER JOIN e.equipmentType t WHERE t.id = :id
        """;
        return session.createQuery(jpqlFindTypeHasConnections, Boolean.class)
            .setParameter("id", typeId)
            .getSingleResult();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void deleteEquipmentTypeById(Object typeId) {
        session.createMutationQuery("DELETE EquipmentTypeEntity e WHERE e.id = :id")
            .setParameter("id", typeId).executeUpdate();
    }
}
