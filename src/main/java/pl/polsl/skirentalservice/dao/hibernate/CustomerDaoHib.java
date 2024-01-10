/*
 * Copyright (c) 2024 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.dao.hibernate;

import org.hibernate.Session;
import pl.polsl.skirentalservice.core.servlet.pageable.FilterDataDto;
import pl.polsl.skirentalservice.dao.CustomerDao;
import pl.polsl.skirentalservice.dao.core.AbstractHibernateDao;
import pl.polsl.skirentalservice.dto.PageableDto;
import pl.polsl.skirentalservice.dto.customer.AddEditCustomerReqDto;
import pl.polsl.skirentalservice.dto.customer.CustomerDetailsResDto;
import pl.polsl.skirentalservice.dto.customer.CustomerRecordResDto;
import pl.polsl.skirentalservice.dto.deliv_return.CustomerDetailsReturnResDto;
import pl.polsl.skirentalservice.util.RentStatus;

import java.util.List;
import java.util.Optional;

public class CustomerDaoHib extends AbstractHibernateDao implements CustomerDao {
    public CustomerDaoHib(Session session) {
        super(session);
    }

    @Override
    public boolean checkIfCustomerExist(Object rentId) {
        final String jpqlFindCustomerExist = """
                SELECT COUNT(c.id) > 0 FROM RentEntity r INNER JOIN r.customer c WHERE r.id = :rid
            """;
        return session.createQuery(jpqlFindCustomerExist, Boolean.class)
            .setParameter("rid", rentId)
            .getSingleResult();
    }

    @Override
    public boolean checkIfCustomerHasAnyActiveRents(Object customerId) {
        final String jpqlCheckIfHasAnyRents = """
                SELECT COUNT(r.id) > 0 FROM RentEntity r INNER JOIN r.customer c
                WHERE c.id = :cid AND r.status <> :st
            """;
        return session.createQuery(jpqlCheckIfHasAnyRents, Boolean.class)
            .setParameter("cid", customerId)
            .setParameter("st", RentStatus.RETURNED)
            .getSingleResult();
    }

    @Override
    public Optional<CustomerDetailsResDto> findCustomerDetails(Object customerId) {
        final String jpqlFindCustomerDetails = """
                SELECT new pl.polsl.skirentalservice.dto.customer.CustomerDetailsResDto(
                    c.id, CONCAT(d.firstName, ' ', d.lastName), d.emailAddress, CAST(d.bornDate AS string),
                    d.pesel, CONCAT('+', d.phoneAreaCode, ' ',
                    SUBSTRING(d.phoneNumber, 1, 3), ' ', SUBSTRING(d.phoneNumber, 4, 3), ' ',
                    SUBSTRING(d.phoneNumber, 7, 3)), YEAR(NOW()) - YEAR(d.bornDate),
                    d.gender, CONCAT(a.postalCode, ' ', a.city),
                    CONCAT('ul. ', a.street, ' ', a.buildingNo, IF(a.apartmentNo, CONCAT('/', a.apartmentNo), ''))
                ) FROM CustomerEntity c INNER JOIN c.userDetails d INNER JOIN c.locationAddress a
                WHERE c.id = :uid
            """;
        final var customerDetails = session.createQuery(jpqlFindCustomerDetails, CustomerDetailsResDto.class)
            .setParameter("uid", customerId)
            .getSingleResultOrNull();
        return Optional.ofNullable(customerDetails);
    }

    @Override
    public Optional<CustomerDetailsReturnResDto> findCustomerDetailsForReturnDocument(Object rentId) {
        final String jpqlGetCustomerDetails = """
                SELECT new pl.polsl.skirentalservice.dto.deliv_return.CustomerDetailsReturnResDto(
                    CONCAT(d.firstName, ' ', d.lastName), d.pesel, CONCAT('+', d.phoneAreaCode, ' ',
                    SUBSTRING(d.phoneNumber, 1, 3), ' ', SUBSTRING(d.phoneNumber, 4, 3), ' ',
                    SUBSTRING(d.phoneNumber, 7, 3)), d.emailAddress, CONCAT('ul. ', a.street, ' ', a.buildingNo,
                    IF(a.apartmentNo, CONCAT('/', a.apartmentNo), ''), ', ', a.postalCode, ' ', a.city)
                ) FROM RentEntity r
                INNER JOIN r.customer c INNER JOIN c.userDetails d INNER JOIN c.locationAddress a
                WHERE r.id = :rentid
            """;
        final CustomerDetailsReturnResDto customerDetails = session
            .createQuery(jpqlGetCustomerDetails, CustomerDetailsReturnResDto.class)
            .setParameter("rentid", rentId)
            .getSingleResultOrNull();
        return Optional.ofNullable(customerDetails);
    }

    @Override
    public Optional<AddEditCustomerReqDto> findCustomerEditPageDetails(Object customerId) {
        final String jpqlFindCustomerBaseId = """
                SELECT new pl.polsl.skirentalservice.dto.customer.AddEditCustomerReqDto(
                    d.firstName, d.lastName, d.pesel,
                    CONCAT(SUBSTRING(d.phoneNumber, 1, 3), ' ', SUBSTRING(d.phoneNumber, 4, 3), ' ',
                    SUBSTRING(d.phoneNumber, 7, 3)),
                    CAST(d.bornDate AS string), d.emailAddress, a.street,
                    a.buildingNo, a.apartmentNo, a.city, a.postalCode, d.gender
                ) FROM CustomerEntity c
                INNER JOIN c.userDetails d INNER JOIN c.locationAddress a
                WHERE c.id = :uid
            """;
        final AddEditCustomerReqDto customerDetails = session
            .createQuery(jpqlFindCustomerBaseId, AddEditCustomerReqDto.class)
            .setParameter("uid", customerId)
            .getSingleResultOrNull();
        return Optional.ofNullable(customerDetails);
    }

    @Override
    public Long findAllCustomersCount(FilterDataDto filterData) {
        String jpqlFindAll = """
                SELECT COUNT(c.id) FROM CustomerEntity c
                INNER JOIN c.userDetails d INNER JOIN c.locationAddress a
                WHERE :searchColumn LIKE :search
            """;
        jpqlFindAll = jpqlFindAll.replace(":searchColumn", filterData.getSearchColumn());
        return session.createQuery(jpqlFindAll, Long.class)
            .setParameter("search", "%" + filterData.getSearchText() + "%")
            .getSingleResult();
    }

    @Override
    public List<CustomerRecordResDto> findAllPageableCustomers(PageableDto pageableDto, String addressColumn) {
        String jpqlFindAllCustomers = """
                SELECT new pl.polsl.skirentalservice.dto.customer.CustomerRecordResDto(
                    c.id, CONCAT(d.firstName, ' ', d.lastName), d.emailAddress, d.pesel,
                    CONCAT('+', d.phoneAreaCode, ' ', SUBSTRING(d.phoneNumber, 1, 3), ' ',
                    SUBSTRING(d.phoneNumber, 4, 3), ' ', SUBSTRING(d.phoneNumber, 7, 3)), :addressColumn
                ) FROM CustomerEntity c
                INNER JOIN c.userDetails d INNER JOIN c.locationAddress a
                WHERE :searchColumn LIKE :search
                ORDER BY :sortedColumn
            """;
        jpqlFindAllCustomers = jpqlFindAllCustomers
            .replace(":addressColumn", addressColumn)
            .replace(":searchColumn", pageableDto.filterData().getSearchColumn())
            .replace(":sortedColumn", pageableDto.sorterData().getJpql());
        return session
            .createQuery(jpqlFindAllCustomers, CustomerRecordResDto.class)
            .setParameter("search", "%" + pageableDto.filterData().getSearchText() + "%")
            .setFirstResult((pageableDto.page() - 1) * pageableDto.total())
            .setMaxResults(pageableDto.total())
            .getResultList();
    }
}
