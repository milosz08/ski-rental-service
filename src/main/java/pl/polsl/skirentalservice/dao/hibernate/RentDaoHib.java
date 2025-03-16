package pl.polsl.skirentalservice.dao.hibernate;

import org.hibernate.Session;
import pl.polsl.skirentalservice.core.servlet.pageable.FilterDataDto;
import pl.polsl.skirentalservice.dao.RentDao;
import pl.polsl.skirentalservice.dao.core.AbstractHibernateDao;
import pl.polsl.skirentalservice.dto.PageableDto;
import pl.polsl.skirentalservice.dto.deliv_return.RentReturnDetailsResDto;
import pl.polsl.skirentalservice.dto.rent.OwnerRentRecordResDto;
import pl.polsl.skirentalservice.dto.rent.RentDetailsResDto;
import pl.polsl.skirentalservice.dto.rent.SellerRentRecordResDto;
import pl.polsl.skirentalservice.entity.RentEntity;
import pl.polsl.skirentalservice.util.RentStatus;

import java.util.List;
import java.util.Optional;

public class RentDaoHib extends AbstractHibernateDao implements RentDao {
    public RentDaoHib(Session session) {
        super(session);
    }

    @Override
    public Optional<RentReturnDetailsResDto> findRentReturnDetails(Object rentId, Object employerId) {
        final String jpqlFindRent = """
                SELECT new pl.polsl.skirentalservice.dto.deliv_return.RentReturnDetailsResDto(
                    r.issuedIdentifier, r.rentDateTime, r.tax,
                    r.totalPrice, CAST((r.tax / 100) * r.totalPrice + r.totalPrice AS bigdecimal),
                    r.totalDepositPrice, CAST((r.tax / 100) * r.totalDepositPrice + r.totalDepositPrice AS bigdecimal)
                ) FROM RentEntity r INNER JOIN r.employer e
                WHERE r.id = :rentid AND e.id = :eid
            """;
        final RentReturnDetailsResDto rentDetails = session.createQuery(jpqlFindRent, RentReturnDetailsResDto.class)
            .setParameter("rentid", rentId)
            .setParameter("eid", employerId)
            .getSingleResultOrNull();
        return Optional.ofNullable(rentDetails);
    }

    @Override
    public Optional<RentDetailsResDto> findRentDetails(Object rentId, Object employerId, String roleAlias) {
        final String jpqlFindRentDetails = """
                SELECT new pl.polsl.skirentalservice.dto.rent.RentDetailsResDto(
                    r.id, r.issuedIdentifier, r.issuedDateTime, r.rentDateTime, r.returnDateTime,
                    IFNULL(r.description, '<i>Brak danych</i>'),
                    r.tax, r.status,
                    CASE WHEN r.status = pl.polsl.skirentalservice.util.RentStatus.RENTED THEN true ELSE false END,
                    r.totalPrice, CAST((r.tax / 100) * r.totalPrice + r.totalPrice AS bigdecimal),
                    r.totalDepositPrice, CAST((r.tax / 100) * r.totalDepositPrice + r.totalDepositPrice AS bigdecimal),
                    CONCAT(d.firstName, ' ', d.lastName), d.pesel, d.bornDate, CONCAT('+', d.phoneAreaCode,
                    SUBSTRING(d.phoneNumber, 1, 3), ' ', SUBSTRING(d.phoneNumber, 4, 3), ' ',
                    SUBSTRING(d.phoneNumber, 7, 3)), YEAR(NOW()) - YEAR(d.bornDate), d.emailAddress,
                    d.gender, CONCAT(a.postalCode, ' ', a.city),
                    CONCAT('ul. ', a.street, ' ', a.buildingNo, IF(a.apartmentNo, CONCAT('/', a.apartmentNo), ''))
                ) FROM RentEntity r
                LEFT OUTER JOIN r.employer e INNER JOIN e.role rl
                LEFT OUTER JOIN r.customer c LEFT OUTER JOIN c.userDetails d LEFT OUTER JOIN c.locationAddress a
                WHERE r.id = :rid AND (e.id = :eid OR :ralias = 'K')
            """;
        final RentDetailsResDto equipmentDetails = session.createQuery(jpqlFindRentDetails, RentDetailsResDto.class)
            .setParameter("rid", rentId)
            .setParameter("eid", employerId)
            .setParameter("ralias", roleAlias)
            .getSingleResultOrNull();
        return Optional.ofNullable(equipmentDetails);
    }

    @Override
    public boolean checkIfRentExist(Object rentId) {
        final String jpqlFindRent = "SELECT COUNT(r.id) > 0 FROM RentEntity r WHERE r.id = :rid";
        return session.createQuery(jpqlFindRent, Boolean.class)
            .setParameter("rid", rentId)
            .getSingleResult();

    }

    @Override
    public boolean checkIfRentIsFromEmployer(Object rentId, Object employerId) {
        final String jpqlFindRentEmployer = """
                SELECT COUNT(r.id) > 0 FROM RentEntity r INNER JOIN r.employer e WHERE e.id = :eid AND r.id = :rid
            """;
        return session.createQuery(jpqlFindRentEmployer, Boolean.class)
            .setParameter("eid", employerId)
            .setParameter("rid", rentId)
            .getSingleResult();
    }

    @Override
    public boolean checkIfIssuerExist(String issuer) {
        final String jpqlFindExistingIssuer = """
                SELECT COUNT(r.id) > 0 FROM RentEntity r WHERE SUBSTRING(r.issuedIdentifier, 4, 4) = :issuer
            """;
        return session.createQuery(jpqlFindExistingIssuer, Boolean.class)
            .setParameter("issuer", issuer)
            .getSingleResult();
    }

    @Override
    public void updateRentStatus(RentStatus rentStatus, Object rentId) {
        final String jpqlUpdateRentStatus = "UPDATE RentEntity r SET r.status = :rst WHERE r.id = :rentid";
        session.createMutationQuery(jpqlUpdateRentStatus)
            .setParameter("rst", rentStatus)
            .setParameter("rentid", rentId)
            .executeUpdate();
    }

    @Override
    public Long findAllRentsCount(FilterDataDto filterData) {
        String jpqlTotalRentsCount = """
                SELECT COUNT(r.id) FROM RentEntity r
                LEFT OUTER JOIN r.employer e LEFT OUTER JOIN r.customer c
                LEFT OUTER JOIN c.userDetails d LEFT OUTER JOIN e.userDetails ed
                WHERE :searchColumn LIKE :search
            """;
        jpqlTotalRentsCount = jpqlTotalRentsCount.replace(":searchColumn", filterData.getSearchColumn());
        return session.createQuery(jpqlTotalRentsCount, Long.class)
            .setParameter("search", "%" + filterData.getSearchText() + "%")
            .getSingleResult();
    }

    @Override
    public Long findAllRentsFromEmployerCount(FilterDataDto filterData, Object employerId) {
        String jpqlTotalRentsCount = """
                SELECT COUNT(r.id) FROM RentEntity r
                LEFT OUTER JOIN r.employer e LEFT OUTER JOIN r.customer c LEFT OUTER JOIN c.userDetails d
                WHERE e.id = :eid AND :searchColumn LIKE :search
            """;
        jpqlTotalRentsCount = jpqlTotalRentsCount.replace(":searchColumn", filterData.getSearchColumn());
        return session.createQuery(jpqlTotalRentsCount, Long.class)
            .setParameter("search", "%" + filterData.getSearchText() + "%")
            .setParameter("eid", employerId)
            .getSingleResult();
    }

    @Override
    public List<RentEntity> findAllRentsBaseCustomerId(Object customerId) {
        final String jpqlFindAllRents = "SELECT r FROM RentEntity r INNER JOIN r.customer c WHERE c.id = :cid";
        return session.createQuery(jpqlFindAllRents, RentEntity.class)
            .setParameter("cid", customerId)
            .getResultList();
    }

    @Override
    public List<OwnerRentRecordResDto> findAllPageableRents(PageableDto pageableDto) {
        String jpqlFindAllRents = """
                SELECT new pl.polsl.skirentalservice.dto.rent.OwnerRentRecordResDto(
                    r.id, r.issuedIdentifier, r.issuedDateTime, r.status, r.totalPrice,
                    CAST((r.tax / 100) * r.totalPrice + r.totalPrice AS bigdecimal),
                    IFNULL(CONCAT(d.firstName, ' ', d.lastName), '<i>klient usunięty</i>'), c.id,
                    IFNULL(CONCAT(ed.firstName, ' ', ed.lastName), '<i>pracownik usunięty</i>'), ed.id
                ) FROM RentEntity r
                LEFT OUTER JOIN r.employer e LEFT OUTER JOIN r.customer c
                LEFT OUTER JOIN c.userDetails d LEFT OUTER JOIN e.userDetails ed
                WHERE :searchColumn LIKE :search
                ORDER BY :sortedColumn
            """;
        jpqlFindAllRents = jpqlFindAllRents
            .replace(":searchColumn", pageableDto.filterData().getSearchColumn())
            .replace(":sortedColumn", pageableDto.sorterData().getJpql());
        return session.createQuery(jpqlFindAllRents, OwnerRentRecordResDto.class)
            .setParameter("search", "%" + pageableDto.filterData().getSearchText() + "%")
            .setFirstResult((pageableDto.page() - 1) * pageableDto.total())
            .setMaxResults(pageableDto.total())
            .getResultList();
    }

    @Override
    public List<SellerRentRecordResDto> findAllPageableRentsFromEmployer(PageableDto pageableDto, Object employerId) {
        String jpqlFindAllRentsConnectedWithEmployer = """
                SELECT new pl.polsl.skirentalservice.dto.rent.SellerRentRecordResDto(
                    r.id, r.issuedIdentifier, r.issuedDateTime, r.status,
                    CASE WHEN r.status = pl.polsl.skirentalservice.util.RentStatus.RENTED THEN true ELSE false END,
                    r.totalPrice,
                    CAST((r.tax / 100) * r.totalPrice + r.totalPrice AS bigdecimal),
                    IFNULL(CONCAT(d.firstName, ' ', d.lastName), '<i>klient usunięty</i>'), c.id
                ) FROM RentEntity r
                INNER JOIN r.employer e LEFT OUTER JOIN r.customer c LEFT OUTER JOIN c.userDetails d
                WHERE e.id = :eid AND :searchColumn LIKE :search
                ORDER BY :sortedColumn
            """;
        jpqlFindAllRentsConnectedWithEmployer = jpqlFindAllRentsConnectedWithEmployer
            .replace(":searchColumn", pageableDto.filterData().getSearchColumn())
            .replace(":sortedColumn", pageableDto.sorterData().getJpql());
        return session.createQuery(jpqlFindAllRentsConnectedWithEmployer, SellerRentRecordResDto.class)
            .setParameter("search", "%" + pageableDto.filterData().getSearchText() + "%")
            .setParameter("eid", employerId)
            .setFirstResult((pageableDto.page() - 1) * pageableDto.total())
            .setMaxResults(pageableDto.total())
            .getResultList();
    }
}
