/*
 * Copyright (c) 2024 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.dao.hibernate;

import org.hibernate.Session;
import pl.polsl.skirentalservice.dao.EquipmentColorDao;
import pl.polsl.skirentalservice.dao.core.AbstractHibernateDao;
import pl.polsl.skirentalservice.dto.FormSelectTupleDto;

import java.util.List;
import java.util.Optional;

public class EquipmentColorDaoHib extends AbstractHibernateDao implements EquipmentColorDao {
    public EquipmentColorDaoHib(Session session) {
        super(session);
    }

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
        return Optional.ofNullable(colorName);
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
                SELECT COUNT(e.id) > 0 FROM EquipmentEntity e INNER JOIN e.color c WHERE c.id = :id
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
