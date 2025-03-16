package pl.polsl.skirentalservice.dao.hibernate;

import org.hibernate.Session;
import pl.polsl.skirentalservice.dao.EquipmentBrandDao;
import pl.polsl.skirentalservice.dao.core.AbstractHibernateDao;
import pl.polsl.skirentalservice.dto.FormSelectTupleDto;

import java.util.List;
import java.util.Optional;

public class EquipmentBrandDaoHib extends AbstractHibernateDao implements EquipmentBrandDao {
    public EquipmentBrandDaoHib(Session session) {
        super(session);
    }

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

    @Override
    public Optional<String> getEquipmentBrandNameById(Object brandId) {
        final String jqplFindNameOfBrand = "SELECT b.name FROM EquipmentBrandEntity b WHERE b.id = :id";
        final String brandName = session.createQuery(jqplFindNameOfBrand, String.class)
            .setParameter("id", brandId)
            .getSingleResultOrNull();
        return Optional.ofNullable(brandName);
    }

    @Override
    public boolean checkIfEquipmentBrandExistByName(String brandName) {
        final String jpqlFindBrandAlreadyExist = """
                SELECT COUNT(b.id) > 0 FROM EquipmentBrandEntity b WHERE LOWER(b.name) = LOWER(:name)
            """;
        return session.createQuery(jpqlFindBrandAlreadyExist, Boolean.class)
            .setParameter("name", brandName)
            .getSingleResult();
    }

    @Override
    public boolean checkIfEquipmentBrandHasAnyConnections(Object brandId) {
        final String jpqlFindBrandHasConnections = """
                SELECT COUNT(e.id) > 0 FROM EquipmentEntity e INNER JOIN e.brand b WHERE b.id = :id
            """;
        return session.createQuery(jpqlFindBrandHasConnections, Boolean.class)
            .setParameter("id", brandId)
            .getSingleResult();
    }

    @Override
    public void deleteEquipmentBrandById(Object brandId) {
        session.createMutationQuery("DELETE EquipmentBrandEntity b WHERE b.id = :id")
            .setParameter("id", brandId).executeUpdate();
    }
}
