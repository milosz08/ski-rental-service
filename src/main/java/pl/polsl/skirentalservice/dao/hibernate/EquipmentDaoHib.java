/*
 * Copyright (c) 2024 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.dao.hibernate;

import org.hibernate.Session;
import pl.polsl.skirentalservice.dao.EquipmentDao;
import pl.polsl.skirentalservice.dao.core.AbstractHibernateDao;
import pl.polsl.skirentalservice.dto.PageableDto;
import pl.polsl.skirentalservice.dto.deliv_return.RentReturnEquipmentRecordResDto;
import pl.polsl.skirentalservice.dto.equipment.AddEditEquipmentReqDto;
import pl.polsl.skirentalservice.dto.equipment.EquipmentDetailsResDto;
import pl.polsl.skirentalservice.dto.equipment.EquipmentRecordResDto;
import pl.polsl.skirentalservice.dto.rent.EquipmentRentRecordResDto;
import pl.polsl.skirentalservice.dto.rent.RentEquipmentsDetailsResDto;
import pl.polsl.skirentalservice.paging.filter.FilterDataDto;
import pl.polsl.skirentalservice.util.RentStatus;

import java.util.List;
import java.util.Optional;

public class EquipmentDaoHib extends AbstractHibernateDao implements EquipmentDao {
    public EquipmentDaoHib(Session session) {
        super(session);
    }

    @Override
    public void decreaseAvailableSelectedEquipmentCount(Object equipmentId, Object count) {
        final String jpqlDecreaseAvailableEqCount = """
                UPDATE EquipmentEntity e SET e.availableCount = e.availableCount - :rentedCount
                WHERE e.id = :eid
            """;
        session.createMutationQuery(jpqlDecreaseAvailableEqCount)
            .setParameter("eid", equipmentId)
            .setParameter("rentedCount", count)
            .executeUpdate();
    }

    @Override
    public void increaseAvailableSelectedEquipmentCount(Object equipmentId, Object count) {
        final String jpqlIncreaseEquipmentCount = """
                UPDATE EquipmentEntity e SET e.availableCount = e.availableCount + :rentedCount
                WHERE e.id = :eid
            """;
        session.createMutationQuery(jpqlIncreaseEquipmentCount)
            .setParameter("eid", equipmentId)
            .setParameter("rentedCount", count)
            .executeUpdate();
    }

    @Override
    public Optional<EquipmentRentRecordResDto> findEquipmentDetails(Object equipmentId) {
        final String jpqlEquipmentDetails = """
                SELECT new pl.polsl.skirentalservice.dto.rent.EquipmentRentRecordResDto(
                    e.id, e.name, t.name, e.model, e.barcode, e.availableCount, e.pricePerHour,
                    e.priceForNextHour, e.pricePerDay, ''
                ) FROM EquipmentEntity e
                INNER JOIN e.equipmentType t
                WHERE e.id = :id
            """;
        final EquipmentRentRecordResDto equipmentDetails = session
            .createQuery(jpqlEquipmentDetails, EquipmentRentRecordResDto.class)
            .setParameter("id", equipmentId)
            .getSingleResultOrNull();
        return Optional.ofNullable(equipmentDetails);
    }

    @Override
    public Optional<AddEditEquipmentReqDto> findAddEditEquipmentDetails(Object equipmentId) {
        final String jpqlFindEquipmentBaseId = """
                SELECT new pl.polsl.skirentalservice.dto.equipment.AddEditEquipmentReqDto(
                    e.name, e.model, e.description, CAST(e.countInStore AS string), CAST(e.size AS string),
                    CAST(e.pricePerHour AS string), CAST(e.priceForNextHour AS string),
                    CAST(e.pricePerDay AS string), CAST(e.valueCost AS string), CAST(t.id AS string),
                    CAST(b.id AS string), CAST(c.id AS string), e.gender
                ) FROM EquipmentEntity e
                INNER JOIN e.equipmentType t INNER JOIN e.equipmentBrand b INNER JOIN e.equipmentColor c
                WHERE e.id = :eid
            """;
        final AddEditEquipmentReqDto equipmentDetails = session
            .createQuery(jpqlFindEquipmentBaseId, AddEditEquipmentReqDto.class)
            .setParameter("eid", equipmentId)
            .getSingleResultOrNull();
        return Optional.ofNullable(equipmentDetails);
    }

    @Override
    public Optional<EquipmentDetailsResDto> findEquipmentDetailsPage(Object equipmentId) {
        final String jpqlFindEquipmentDetails = """
                SELECT new pl.polsl.skirentalservice.dto.equipment.EquipmentDetailsResDto(
                    e.id, e.name, t.name, e.model, e.gender, e.barcode,
                    CASE WHEN e.size IS NULL THEN '<i>brak danych</i>' ELSE CAST(e.size AS string) END,
                    b.name, c.name, CONCAT(e.availableCount, '/', e.countInStore),
                    e.countInStore - e.availableCount, e.pricePerHour, e.priceForNextHour,
                    e.pricePerDay, e.valueCost,
                    CASE WHEN e.description IS NULL THEN '<i>brak danych</i>' ELSE e.description END
                ) FROM EquipmentEntity e
                INNER JOIN e.equipmentType t INNER JOIN e.equipmentBrand b INNER JOIN e.equipmentColor c
                WHERE e.id = :eid
            """;
        final EquipmentDetailsResDto equipmentDetails = session
            .createQuery(jpqlFindEquipmentDetails, EquipmentDetailsResDto.class)
            .setParameter("eid", equipmentId)
            .getSingleResultOrNull();
        return Optional.ofNullable(equipmentDetails);
    }

    @Override
    public boolean checkIfEquipmentExist(Object equipmentId) {
        final String jpqlEquipmentDetails = """
                SELECT COUNT(e.id) > 0 FROM EquipmentEntity e INNER JOIN e.equipmentType t WHERE e.id = :id
            """;
        return session.createQuery(jpqlEquipmentDetails, Boolean.class)
            .setParameter("id", equipmentId)
            .getSingleResult();
    }

    @Override
    public boolean checkIfEquipmentModelExist(String modelName, Object equipmentId) {
        final String jpqlEquipmentModelExist = """
                SELECT COUNT(e.id) > 0 FROM EquipmentEntity e WHERE LOWER(e.model) = LOWER(:model) AND e.id <> :eid
            """;
        return session.createQuery(jpqlEquipmentModelExist, Boolean.class)
            .setParameter("model", modelName)
            .setParameter("eid", equipmentId)
            .getSingleResult();
    }

    @Override
    public boolean checkIfBarCodeExist(String barcode) {
        final String jpqlFindBarcode = "SELECT COUNT(e.id) > 0 FROM EquipmentEntity e WHERE e.barcode = :barcode";
        return session.createQuery(jpqlFindBarcode, Boolean.class)
            .setParameter("barcode", barcode)
            .getSingleResult();
    }

    @Override
    public boolean checkIfEquipmentHasOpenedRents(Object equipmentId) {
        final String jpqlFindHasConnections = """
                SELECT COUNT(r.id) > 0 FROM RentEntity r INNER JOIN r.equipments e INNER JOIN e.equipment eq
                WHERE eq.id = :eid AND r.status <> :rst
            """;
        return session.createQuery(jpqlFindHasConnections, Boolean.class)
            .setParameter("eid", equipmentId).setParameter("rst", RentStatus.RETURNED)
            .getSingleResult();
    }

    @Override
    public Long getCountIfSomeEquipmentsAreAvailable() {
        final String getAllCounts = "SELECT SUM(e.availableCount) FROM EquipmentEntity e";
        return session.createQuery(getAllCounts, Long.class).getSingleResultOrNull();
    }

    @Override
    public List<RentReturnEquipmentRecordResDto> findAllEquipmentsConnectedWithRentReturn(Object rentId) {
        final String jpqlFindAllEquipmentsConnectedWithRent = """
                SELECT new pl.polsl.skirentalservice.dto.deliv_return.RentReturnEquipmentRecordResDto(
                    re.id, red.pricePerHour, red.priceForNextHour, red.pricePerDay, re.count,
                    re.depositPrice, red.id, re.description
                ) FROM RentEquipmentEntity re
                INNER JOIN re.rent r INNER JOIN r.employer e INNER JOIN e.role rl INNER JOIN re.equipment red
                WHERE r.id = :rentid ORDER BY re.id
            """;
        return session.createQuery(jpqlFindAllEquipmentsConnectedWithRent, RentReturnEquipmentRecordResDto.class)
            .setParameter("rentid", rentId)
            .getResultList();
    }

    @Override
    public List<RentEquipmentsDetailsResDto> findAllEquipmentsConnectedWithReturn(Object returnId) {
        final String jpqlFindAllEquipments = """
                SELECT new pl.polsl.skirentalservice.dto.rent.RentEquipmentsDetailsResDto(
                    re.id, IFNULL(e.name, '<i>sprzęt usunięty</i>'), rer.count, e.barcode, re.description,
                    re.totalPrice, CAST((r.tax / 100) * re.totalPrice + re.totalPrice AS bigdecimal),
                    re.depositPrice, CAST((r.tax / 100) * re.depositPrice + re.depositPrice AS bigdecimal)
                ) FROM RentReturnEquipmentEntity re
                INNER JOIN re.rentEquipment rer
                INNER JOIN rer.rent r INNER JOIN re.rentReturn rrer LEFT OUTER JOIN re.equipment e
                WHERE rrer.id = :rid ORDER BY re.id
            """;
        return session.createQuery(jpqlFindAllEquipments, RentEquipmentsDetailsResDto.class)
            .setParameter("rid", returnId)
            .getResultList();
    }

    @Override
    public List<RentEquipmentsDetailsResDto> findAllEquipmentsConnectedWithRent(Object rentId) {
        final String jpqlFindAllEquipments = """
                SELECT new pl.polsl.skirentalservice.dto.rent.RentEquipmentsDetailsResDto(
                    re.id, IFNULL(e.name, '<i>sprzęt usunięty</i>'), re.count, e.barcode, re.description,
                    re.totalPrice, CAST((r.tax / 100) * re.totalPrice + re.totalPrice AS bigdecimal),
                    re.depositPrice, CAST((r.tax / 100) * re.depositPrice + re.depositPrice AS bigdecimal)
                ) FROM RentEquipmentEntity re
                INNER JOIN re.rent r LEFT OUTER JOIN re.equipment e LEFT OUTER JOIN r.employer emp
                WHERE r.id = :rid ORDER BY re.id
            """;
        return session.createQuery(jpqlFindAllEquipments, RentEquipmentsDetailsResDto.class)
            .setParameter("rid", rentId)
            .getResultList();
    }

    @Override
    public Integer findAllEquipmentsInCartCount(Object equipmentId) {
        final String jpqlCheckEquipmentCount = "SELECT e.availableCount FROM EquipmentEntity e WHERE e.id = :id";
        return session.createQuery(jpqlCheckEquipmentCount, Integer.class)
            .setParameter("id", equipmentId)
            .getSingleResult();
    }

    @Override
    public Long findAllEquipmentsCount(FilterDataDto filterData) {
        String jpqlFindAll = """
                SELECT COUNT(e.id) FROM EquipmentEntity e WHERE :searchColumn LIKE :search
            """;
        jpqlFindAll = jpqlFindAll.replace(":searchColumn", filterData.getSearchColumn());
        return session.createQuery(jpqlFindAll, Long.class)
            .setParameter("search", "%" + filterData.getSearchText() + "%")
            .getSingleResult();
    }

    @Override
    public List<EquipmentRecordResDto> findAllPageableEquipmentRecords(PageableDto pageableDto) {
        String jpqlFindAllEquipments = """
                SELECT new pl.polsl.skirentalservice.dto.equipment.EquipmentRecordResDto(
                    e.id, e.name, t.name, e.barcode, e.availableCount, e.pricePerHour, e.priceForNextHour,
                    e.pricePerDay, e.valueCost
                ) FROM EquipmentEntity e
                INNER JOIN e.equipmentType t
                WHERE :searchColumn LIKE :search
                ORDER BY :sortedColumn
            """;
        jpqlFindAllEquipments = jpqlFindAllEquipments
            .replace(":searchColumn", pageableDto.filterData().getSearchColumn())
            .replace(":sortedColumn", pageableDto.sorterData().getJpql());
        return session.createQuery(jpqlFindAllEquipments, EquipmentRecordResDto.class)
            .setParameter("search", "%" + pageableDto.filterData().getSearchText() + "%")
            .setFirstResult((pageableDto.page() - 1) * pageableDto.total())
            .setMaxResults(pageableDto.total())
            .getResultList();
    }

    @Override
    public List<EquipmentRentRecordResDto> findAllPageableEquipments(PageableDto pageableDto) {
        String jpqlFindAllEquipments = """
                SELECT new pl.polsl.skirentalservice.dto.rent.EquipmentRentRecordResDto(
                    e.id, e.name, t.name, e.model, e.barcode, e.availableCount, e.pricePerHour,
                    e.priceForNextHour, e.pricePerDay, ''
                ) FROM EquipmentEntity e
                INNER JOIN e.equipmentType t
                WHERE :searchColumn LIKE :search GROUP BY e.id
                ORDER BY :sortedColumn
            """;
        jpqlFindAllEquipments = jpqlFindAllEquipments
            .replace(":searchColumn", pageableDto.filterData().getSearchColumn())
            .replace(":sortedColumn", pageableDto.sorterData().getJpql());
        return session.createQuery(jpqlFindAllEquipments, EquipmentRentRecordResDto.class)
            .setParameter("search", "%" + pageableDto.filterData().getSearchText() + "%")
            .setFirstResult((pageableDto.page() - 1) * pageableDto.total())
            .setMaxResults(pageableDto.total())
            .getResultList();
    }
}
