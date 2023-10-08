/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.dao.equipment_color;

import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import pl.polsl.skirentalservice.dto.FormSelectTupleDto;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
public class EquipmentColorDao implements IEquipmentColorDao {

    private final Session session;

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

    @Override
    public Optional<String> getEquipmentColorNameById(Object colorId) {
        final String jqplFindNameOfColor = "SELECT c.name FROM EquipmentColorEntity c WHERE c.id = :id";
        final String colorName = session.createQuery(jqplFindNameOfColor, String.class)
            .setParameter("id", colorId)
            .getSingleResultOrNull();
        if (Objects.isNull(colorName)) return Optional.empty();
        return Optional.of(colorName);
    }

    @Override
    public boolean checkIfEquipmentColorExistByName(String colorName) {
        final String jpqlFindColorAlreadyExist = """
                SELECT COUNT(c.id) > 0 FROM EquipmentColorEntity c WHERE LOWER(c.name) = LOWER(:name)
            """;
        return session.createQuery(jpqlFindColorAlreadyExist, Boolean.class)
            .setParameter("name", colorName)
            .getSingleResult();
    }

    @Override
    public boolean checkIfEquipmentColorHasAnyConnections(Object colorId) {
        final String jpqlFindColorHasConnections = """
                SELECT COUNT(e.id) > 0 FROM EquipmentEntity e INNER JOIN e.equipmentColor c WHERE c.id = :id
            """;
        return session.createQuery(jpqlFindColorHasConnections, Boolean.class)
            .setParameter("id", colorId)
            .getSingleResult();
    }

    @Override
    public void deleteEquipmentColorById(Object colorId) {
        session.createMutationQuery("DELETE EquipmentColorEntity c WHERE c.id = :id")
            .setParameter("id", colorId).executeUpdate();
    }
}
