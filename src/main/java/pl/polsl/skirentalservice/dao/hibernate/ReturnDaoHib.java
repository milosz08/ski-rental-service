/*
 * Copyright (c) 2024 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.dao.hibernate;

import org.hibernate.Session;
import pl.polsl.skirentalservice.core.servlet.pageable.FilterDataDto;
import pl.polsl.skirentalservice.dao.ReturnDao;
import pl.polsl.skirentalservice.dao.core.AbstractHibernateDao;
import pl.polsl.skirentalservice.dto.PageableDto;
import pl.polsl.skirentalservice.dto.deliv_return.OwnerRentReturnRecordResDto;
import pl.polsl.skirentalservice.dto.deliv_return.ReturnAlreadyExistPayloadDto;
import pl.polsl.skirentalservice.dto.deliv_return.ReturnRentDetailsResDto;
import pl.polsl.skirentalservice.dto.deliv_return.SellerRentReturnRecordResDto;

import java.util.List;
import java.util.Optional;

public class ReturnDaoHib extends AbstractHibernateDao implements ReturnDao {
    public ReturnDaoHib(Session session) {
        super(session);
    }

    @Override
    public Optional<ReturnAlreadyExistPayloadDto> findReturnExistDocument(Object rentId) {
        final String jpqlFindReturn = """
                SELECT new pl.polsl.skirentalservice.dto.deliv_return.ReturnAlreadyExistPayloadDto(
                    re.id, re.issuedIdentifier
                ) FROM RentReturnEntity re INNER JOIN re.rent r WHERE r.id = :rid
            """;
        final ReturnAlreadyExistPayloadDto returnAlreadyExist = session
            .createQuery(jpqlFindReturn, ReturnAlreadyExistPayloadDto.class)
            .setParameter("rid", rentId)
            .getSingleResultOrNull();
        return Optional.ofNullable(returnAlreadyExist);
    }

    @Override
    public Optional<ReturnRentDetailsResDto> findReturnDetails(Object returnId, Object employerId, String roleAlias) {
        final String jpqlFindReturnDetails = """
                SELECT new pl.polsl.skirentalservice.dto.deliv_return.ReturnRentDetailsResDto(
                    rr.id, rr.issuedIdentifier, r.issuedIdentifier, rr.issuedDateTime, rr.description, r.tax,
                    rr.totalPrice, CAST((r.tax / 100) * rr.totalPrice + rr.totalPrice AS bigdecimal), rr.totalDepositPrice,
                    CAST((r.tax / 100) * rr.totalDepositPrice + rr.totalDepositPrice AS bigdecimal),
                    CONCAT(d.firstName, ' ', d.lastName), d.pesel, d.bornDate, CONCAT('+', d.phoneAreaCode,
                    SUBSTRING(d.phoneNumber, 1, 3), ' ', SUBSTRING(d.phoneNumber, 4, 3), ' ',
                    SUBSTRING(d.phoneNumber, 7, 3)), YEAR(NOW()) - YEAR(d.bornDate), d.emailAddress,
                    d.gender, CONCAT(a.postalCode, ' ', a.city),
                    CONCAT('ul. ', a.street, ' ', a.buildingNo, IF(a.apartmentNo, CONCAT('/', a.apartmentNo), ''))
                ) FROM RentReturnEntity rr
                LEFT OUTER JOIN rr.rent r
                LEFT OUTER JOIN r.employer e LEFT OUTER JOIN r.customer c LEFT OUTER JOIN c.userDetails d
                LEFT OUTER JOIN c.locationAddress a
                WHERE rr.id = :rid AND (e.id = :eid OR :ralias = 'K')
            """;
        final ReturnRentDetailsResDto returnDetails = session
            .createQuery(jpqlFindReturnDetails, ReturnRentDetailsResDto.class)
            .setParameter("rid", returnId)
            .setParameter("eid", employerId)
            .setParameter("ralias", roleAlias)
            .getSingleResultOrNull();
        return Optional.ofNullable(returnDetails);
    }

    @Override
    public Long findAllReturnsCount(FilterDataDto filterData) {
        String jpqlTotalReturnsCount = """
                SELECT COUNT(r.id) FROM RentReturnEntity r
                INNER JOIN r.rent rd INNER JOIN rd.employer e INNER JOIN e.userDetails ed
                WHERE :searchColumn LIKE :search
            """;
        jpqlTotalReturnsCount = jpqlTotalReturnsCount.replace(":searchColumn", filterData.getSearchColumn());
        return session.createQuery(jpqlTotalReturnsCount, Long.class)
            .setParameter("search", "%" + filterData.getSearchText() + "%")
            .getSingleResult();
    }

    @Override
    public Long findAllReturnsFromEmployerCount(FilterDataDto filterData, Object employerId) {
        String jpqlTotalReturnsCount = """
                SELECT COUNT(r.id) FROM RentReturnEntity r
                INNER JOIN r.rent rd INNER JOIN rd.employer e
                WHERE e.id = :eid AND :searchColumn LIKE :search
            """;
        jpqlTotalReturnsCount = jpqlTotalReturnsCount.replace(":searchColumn", filterData.getSearchColumn());
        return session.createQuery(jpqlTotalReturnsCount, Long.class)
            .setParameter("search", "%" + filterData.getSearchText() + "%")
            .setParameter("eid", employerId)
            .getSingleResult();
    }

    @Override
    public List<OwnerRentReturnRecordResDto> findAllPageableReturnsRecords(PageableDto pageableDto) {
        String jpqlFindAlReturns = """
                SELECT new pl.polsl.skirentalservice.dto.deliv_return.OwnerRentReturnRecordResDto(
                    r.id, r.issuedIdentifier, r.issuedDateTime, r.totalPrice,
                    CAST((rd.tax / 100) * r.totalPrice + r.totalPrice AS bigdecimal), rd.id, rd.issuedIdentifier,
                    e.id, CONCAT(ed.firstName, ' ', ed.lastName)
                ) FROM RentReturnEntity r
                INNER JOIN r.rent rd INNER JOIN rd.employer e INNER JOIN e.userDetails ed
                WHERE :searchColumn LIKE :search
                ORDER BY :sortedColumn
            """;
        jpqlFindAlReturns = jpqlFindAlReturns
            .replace(":searchColumn", pageableDto.filterData().getSearchColumn())
            .replace(":sortedColumn", pageableDto.sorterData().getJpql());
        return session
            .createQuery(jpqlFindAlReturns, OwnerRentReturnRecordResDto.class)
            .setParameter("search", "%" + pageableDto.filterData().getSearchText() + "%")
            .setFirstResult((pageableDto.page() - 1) * pageableDto.total())
            .setMaxResults(pageableDto.total())
            .getResultList();
    }

    @Override
    public List<SellerRentReturnRecordResDto> findAllPageableReturnsFromEmployerRecords(PageableDto pageableDto, Object employerId) {
        String jpqlFindAlReturnsConnectedWithEmployer = """
                SELECT new pl.polsl.skirentalservice.dto.deliv_return.SellerRentReturnRecordResDto(
                    r.id, r.issuedIdentifier, r.issuedDateTime, r.totalPrice,
                    CAST((rd.tax / 100) * r.totalPrice + r.totalPrice AS bigdecimal), rd.id, rd.issuedIdentifier
                ) FROM RentReturnEntity r
                INNER JOIN r.rent rd INNER JOIN rd.employer e
                WHERE e.id = :eid AND :searchColumn LIKE :search
                ORDER BY :sortedColumn
            """;
        jpqlFindAlReturnsConnectedWithEmployer = jpqlFindAlReturnsConnectedWithEmployer
            .replace(":searchColumn", pageableDto.filterData().getSearchColumn())
            .replace(":sortedColumn", pageableDto.sorterData().getJpql());
        return session
            .createQuery(jpqlFindAlReturnsConnectedWithEmployer, SellerRentReturnRecordResDto.class)
            .setParameter("search", "%" + pageableDto.filterData().getSearchText() + "%")
            .setParameter("eid", employerId)
            .setFirstResult((pageableDto.page() - 1) * pageableDto.total())
            .setMaxResults(pageableDto.total())
            .getResultList();
    }

    @Override
    public boolean checkIfReturnExist(Object returnId) {
        final String jpqlReturnExist = "SELECT COUNT(r.id) > 0 FROM RentReturnEntity r WHERE r.id = :rid";
        return session.createQuery(jpqlReturnExist, Boolean.class)
            .setParameter("rid", returnId)
            .getSingleResult();
    }

    @Override
    public boolean checkIfReturnIsFromEmployer(Object returnId, Object employerId) {
        final String jpqlFindRentEmployer = """
                SELECT COUNT(r.id) > 0 FROM RentReturnEntity r
                INNER JOIN r.rent re INNER JOIN re.employer e
                WHERE e.id = :eid AND r.id = :rid
            """;
        return session.createQuery(jpqlFindRentEmployer, Boolean.class)
            .setParameter("eid", employerId)
            .setParameter("rid", returnId)
            .getSingleResult();
    }
}
