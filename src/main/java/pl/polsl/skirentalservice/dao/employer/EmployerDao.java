/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: EmployerDao.java
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

package pl.polsl.skirentalservice.dao.employer;

import lombok.RequiredArgsConstructor;

import org.hibernate.Session;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import pl.polsl.skirentalservice.util.RentStatus;
import pl.polsl.skirentalservice.dto.PageableDto;
import pl.polsl.skirentalservice.paging.filter.FilterDataDto;
import pl.polsl.skirentalservice.dto.OwnerMailPayloadDto;
import pl.polsl.skirentalservice.dto.employer.AddEditEmployerReqDto;
import pl.polsl.skirentalservice.dto.employer.EmployerDetailsResDto;
import pl.polsl.skirentalservice.dto.employer.EmployerRecordResDto;
import pl.polsl.skirentalservice.dto.login.LoggedUserDataDto;
import pl.polsl.skirentalservice.dto.change_password.EmployerDetailsDto;
import pl.polsl.skirentalservice.entity.EmployerEntity;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@RequiredArgsConstructor
public class EmployerDao implements IEmployerDao {

    private final Session session;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Optional<String> findEmployerPassword(String loginOrEmail) {
        final String jpqlFindEmployer = """
            SELECT e.password FROM EmployerEntity e INNER JOIN e.userDetails d
            WHERE e.login = :loginOrEmail OR d.emailAddress = :loginOrEmail
        """;
        final String password = session.createQuery(jpqlFindEmployer, String.class)
            .setParameter("loginOrEmail", loginOrEmail)
            .getSingleResultOrNull();
        if (Objects.isNull(password)) return Optional.empty();
        return Optional.of(password);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Optional<EmployerEntity> findEmployerBasedId(Object employerId) {
        final String jpqlFindEmployer = """
            SELECT e FROM EmployerEntity e INNER JOIN e.role r
            INNER JOIN e.userDetails d WHERE e.id = :employerId AND r.id <> 2
        """;
        final EmployerEntity employer = session.createQuery(jpqlFindEmployer, EmployerEntity.class)
            .setParameter("employerId", employerId)
            .getSingleResultOrNull();
        if (Objects.isNull(employer)) return Optional.empty();
        return Optional.of(employer);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Optional<LoggedUserDataDto> findLoggedEmployerDetails(String loginOrEmail) {
        final String jpqlSelectEmployer = """
            SELECT new pl.polsl.skirentalservice.dto.login.LoggedUserDataDto(
                e.id, e.login, CONCAT(d.firstName, ' ', d.lastName), r.roleName,
                e.role.alias, e.role.roleEng, d.gender, d.emailAddress, e.firstAccess
            ) FROM EmployerEntity e
            INNER JOIN e.role r
            INNER JOIN e.userDetails d
            WHERE e.login = :loginOrEmail OR d.emailAddress = :loginOrEmail
        """;
        final LoggedUserDataDto employer = session.createQuery(jpqlSelectEmployer, LoggedUserDataDto.class)
            .setParameter("loginOrEmail", loginOrEmail)
            .getSingleResultOrNull();
        if (Objects.isNull(employer)) return Optional.empty();
        return Optional.of(employer);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Optional<EmployerDetailsDto> findEmployerDetails(String loginOrEmail) {
        final String jpqlEmployerDetails = """
            SELECT new pl.polsl.skirentalservice.dto.change_password.EmployerDetailsDto(
                e.id, e.login, CONCAT(d.firstName, ' ', d.lastName), d.emailAddress
            ) FROM EmployerEntity e
            INNER JOIN e.userDetails d
            WHERE e.login = :loginOrEmail OR d.emailAddress = :loginOrEmail
        """;
        final var employer = session.createQuery(jpqlEmployerDetails, EmployerDetailsDto.class)
            .setParameter("loginOrEmail", loginOrEmail)
            .getSingleResultOrNull();
        if (Objects.isNull(employer)) return Optional.empty();
        return Optional.of(employer);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Optional<EmployerDetailsResDto> findEmployerPageDetails(Object employerId) {
        final String jpqlFindEmployerDetails = """
            SELECT new pl.polsl.skirentalservice.dto.employer.EmployerDetailsResDto(
                e.id, CONCAT(d.firstName, ' ', d.lastName), e.login, d.emailAddress, CAST(d.bornDate AS string),
                CAST(e.hiredDate AS string), d.pesel, CONCAT('+', d.phoneAreaCode, ' ',
                SUBSTRING(d.phoneNumber, 1, 3), ' ', SUBSTRING(d.phoneNumber, 4, 3), ' ',
                SUBSTRING(d.phoneNumber, 7, 3)), YEAR(NOW()) - YEAR(d.bornDate),
                YEAR(NOW()) - YEAR(e.hiredDate), d.gender, CONCAT(a.postalCode, ' ', a.city),
                CAST(IF(e.firstAccess, 'nieaktywowane', 'aktywowane') AS string),
                CAST(IF(e.firstAccess, 'text-danger', 'text-success') AS string),
                CONCAT('ul. ', a.street, ' ', a.buildingNr, IF(a.apartmentNr, CONCAT('/', a.apartmentNr), ''))
            ) FROM EmployerEntity e INNER JOIN e.userDetails d INNER JOIN e.locationAddress a
            WHERE e.id = :eid
        """;
        final var employerDetails = session.createQuery(jpqlFindEmployerDetails, EmployerDetailsResDto.class)
            .setParameter("eid", employerId)
            .getSingleResultOrNull();
        if (Objects.isNull(employerDetails)) return Optional.empty();
        return Optional.of(employerDetails);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Optional<AddEditEmployerReqDto> findEmployerEditPageDetails(Object employerId) {
        final String jpqlFindEmployerBaseId = """
            SELECT new pl.polsl.skirentalservice.dto.employer.AddEditEmployerReqDto(
                d.firstName, d.lastName, d.pesel,
                CONCAT(SUBSTRING(d.phoneNumber, 1, 3), ' ', SUBSTRING(d.phoneNumber, 4, 3), ' ',
                SUBSTRING(d.phoneNumber, 7, 3)), CAST(d.bornDate AS string), CAST(e.hiredDate AS string),
                a.street, a.buildingNr, a.apartmentNr, a.city, a.postalCode, d.gender
            ) FROM EmployerEntity e
            INNER JOIN e.userDetails d INNER JOIN e.locationAddress a INNER JOIN e.role r
            WHERE e.id = :uid
        """;
        final AddEditEmployerReqDto employerDetails = session
            .createQuery(jpqlFindEmployerBaseId, AddEditEmployerReqDto.class)
            .setParameter("uid", employerId)
            .getSingleResultOrNull();
        if (Objects.isNull(employerDetails)) return Optional.empty();
        return Optional.of(employerDetails);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void updateEmployerFirstAccessPassword(String newPassword, Object employerId) {
        final String updateUserPassword = """
            UPDATE EmployerEntity e SET e.password = :newPassword, e.firstAccess = false WHERE e.id = :id
        """;
        session.createMutationQuery(updateUserPassword)
            .setParameter("newPassword", newPassword)
            .setParameter("id", employerId)
            .executeUpdate();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void updateEmployerPassword(String newPassword, Object employerId) {
        session.createMutationQuery("UPDATE EmployerEntity e SET e.password = :password WHERE e.id = :employerId")
            .setParameter("password", newPassword)
            .setParameter("employerId", employerId)
            .executeUpdate();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public boolean checkIfLoginAlreadyExist(String login) {
        final String jpqlFindMathEmail = "SELECT COUNT(e.id) > 0 FROM EmployerEntity e WHERE e.login = :loginSeq";
        return session.createQuery(jpqlFindMathEmail, Boolean.class)
            .setParameter("loginSeq", login)
            .getSingleResult();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public boolean checkIfEmployerHasOpenedRents(Object employerId) {
        final String jpqlCheckIfHasAnyRents = """
            SELECT COUNT(r.id) > 0 FROM RentEntity r INNER JOIN r.employer e
            WHERE e.id = :eid AND r.status <> :st
        """;
        return session.createQuery(jpqlCheckIfHasAnyRents, Boolean.class)
            .setParameter("eid", employerId)
            .setParameter("st", RentStatus.RETURNED)
            .getSingleResult();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Long findAllEmployersCount(FilterDataDto filterData) {
        String jpqlTotalEmployersCount = """
            SELECT COUNT(e.id) FROM EmployerEntity e
            INNER JOIN e.userDetails d INNER JOIN e.role r
            WHERE r.id = 1 AND :searchColumn LIKE :search
        """;
        jpqlTotalEmployersCount = jpqlTotalEmployersCount.replace(":searchColumn", filterData.getSearchColumn());
        return session.createQuery(jpqlTotalEmployersCount, Long.class)
            .setParameter("search", "%" + filterData.getSearchText() + "%")
            .getSingleResult();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public List<OwnerMailPayloadDto> findAllEmployersMailSenders() {
        final String jpqlFindAllOwners = """
            SELECT new pl.polsl.skirentalservice.dto.OwnerMailPayloadDto(
                CONCAT(d.firstName, ' ', d.lastName), d.emailAddress
            ) FROM EmployerEntity e
            INNER JOIN e.userDetails d INNER JOIN e.role r WHERE r.alias = 'K'
        """;
        return session
            .createQuery(jpqlFindAllOwners, OwnerMailPayloadDto.class)
            .getResultList();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public List<EmployerRecordResDto> findAllPageableEmployersRecords(PageableDto pageableDto) {
        String jpqlFindAllEmployers = """
            SELECT new pl.polsl.skirentalservice.dto.employer.EmployerRecordResDto(
                e.id, CONCAT(d.firstName, ' ', d.lastName), e.hiredDate, d.pesel, d.emailAddress,
                CONCAT('+', d.phoneAreaCode, ' ', SUBSTRING(d.phoneNumber, 1, 3), ' ',
                SUBSTRING(d.phoneNumber, 4, 3), ' ', SUBSTRING(d.phoneNumber, 7, 3)), d.gender
            ) FROM EmployerEntity e
            INNER JOIN e.userDetails d INNER JOIN e.role r
            WHERE r.id <> 2 AND :searchColumn LIKE :search
            ORDER BY :sortedColumn
        """;
        jpqlFindAllEmployers = jpqlFindAllEmployers
            .replace(":searchColumn", pageableDto.filterData().getSearchColumn())
            .replace(":sortedColumn", pageableDto.sorterData().getJpql());
        return session
            .createQuery(jpqlFindAllEmployers, EmployerRecordResDto.class)
            .setParameter("search", "%" + pageableDto.filterData().getSearchText() + "%")
            .setFirstResult((pageableDto.page() - 1) * pageableDto.total())
            .setMaxResults(pageableDto.total())
            .getResultList();
    }
}
