/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 * Silesian University of Technology
 *
 *  File name: EmployerDao.java
 *  Last modified: 20/02/2023, 18:56
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.dao.employer;

import lombok.RequiredArgsConstructor;

import java.util.*;
import org.hibernate.Session;

import pl.polsl.skirentalservice.dto.employer.*;
import pl.polsl.skirentalservice.entity.EmployerEntity;
import pl.polsl.skirentalservice.dto.OwnerMailPayloadDto;
import pl.polsl.skirentalservice.dto.login.LoggedUserDataDto;
import pl.polsl.skirentalservice.paging.filter.FilterDataDto;
import pl.polsl.skirentalservice.paging.sorter.SorterDataDto;
import pl.polsl.skirentalservice.dto.change_password.EmployerDetailsDto;

import static java.util.Optional.*;
import static java.util.Objects.isNull;

import static pl.polsl.skirentalservice.util.RentStatus.RETURNED;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@RequiredArgsConstructor
public class EmployerDao implements IEmployerDao {

    private final Session session;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Optional<String> findEmployerPassword(String loginOrEmail) {
        final String jpqlFindEmployer =
            "SELECT e.password FROM EmployerEntity e INNER JOIN e.userDetails d " +
            "WHERE e.login = :loginOrEmail OR d.emailAddress = :loginOrEmail";
        final String password = session.createQuery(jpqlFindEmployer, String.class)
            .setParameter("loginOrEmail", loginOrEmail)
            .getSingleResultOrNull();
        if (isNull(password)) return empty();
        return of(password);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Optional<EmployerEntity> findEmployerBasedId(Object employerId) {
        final String jpqlFindEmployer =
            "SELECT e FROM EmployerEntity e INNER JOIN e.role r " +
            "INNER JOIN e.userDetails d WHERE e.id = :employerId AND r.id <> 2";
        final EmployerEntity employer = session.createQuery(jpqlFindEmployer, EmployerEntity.class)
            .setParameter("employerId", employerId)
            .getSingleResultOrNull();
        if (isNull(employer)) return empty();
        return of(employer);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Optional<LoggedUserDataDto> findLoggedEmployerDetails(String loginOrEmail) {
        final String jpqlSelectEmployer =
            "SELECT new pl.polsl.skirentalservice.dto.login.LoggedUserDataDto(" +
                "e.id, e.login, CONCAT(d.firstName, ' ', d.lastName), r.roleName, " +
                "e.role.alias, e.role.roleEng, d.gender, d.emailAddress, e.firstAccess" +
            ") FROM EmployerEntity e " +
            "INNER JOIN e.role r " +
            "INNER JOIN e.userDetails d " +
            "WHERE e.login = :loginOrEmail OR d.emailAddress = :loginOrEmail";
        final LoggedUserDataDto employer = session.createQuery(jpqlSelectEmployer, LoggedUserDataDto.class)
            .setParameter("loginOrEmail", loginOrEmail)
            .getSingleResultOrNull();
        if (isNull(employer)) return empty();
        return of(employer);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Optional<EmployerDetailsDto> findEmployerDetails(String loginOrEmail) {
        final String jpqlEmployerDetails =
            "SELECT new pl.polsl.skirentalservice.dto.change_password.EmployerDetailsDto(" +
                "e.id, e.login, CONCAT(d.firstName, ' ', d.lastName), d.emailAddress " +
            ") FROM EmployerEntity e " +
            "INNER JOIN e.userDetails d " +
            "WHERE e.login = :loginOrEmail OR d.emailAddress = :loginOrEmail";
        final var employer = session.createQuery(jpqlEmployerDetails, EmployerDetailsDto.class)
            .setParameter("loginOrEmail", loginOrEmail)
            .getSingleResultOrNull();
        if (isNull(employer)) return empty();
        return of(employer);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Optional<EmployerDetailsResDto> findEmployerPageDetails(Object employerId) {
        final String jpqlFindEmployerDetails =
            "SELECT new pl.polsl.skirentalservice.dto.employer.EmployerDetailsResDto(" +
                "e.id, CONCAT(d.firstName, ' ', d.lastName), e.login, d.emailAddress, CAST(d.bornDate AS string)," +
                "CAST(e.hiredDate AS string), d.pesel, CONCAT('+', d.phoneAreaCode, ' '," +
                "SUBSTRING(d.phoneNumber, 1, 3), ' ', SUBSTRING(d.phoneNumber, 4, 3), ' '," +
                "SUBSTRING(d.phoneNumber, 7, 3)), YEAR(NOW()) - YEAR(d.bornDate)," +
                "YEAR(NOW()) - YEAR(e.hiredDate), d.gender, CONCAT(a.postalCode, ' ', a.city)," +
                "CAST(IF(e.firstAccess, 'nieaktywowane', 'aktywowane') AS string)," +
                "CAST(IF(e.firstAccess, 'text-danger', 'text-success') AS string)," +
                "CONCAT('ul. ', a.street, ' ', a.buildingNr, IF(a.apartmentNr, CONCAT('/', a.apartmentNr), ''))" +
            ") FROM EmployerEntity e INNER JOIN e.userDetails d INNER JOIN e.locationAddress a " +
            "WHERE e.id = :eid";
        final var employerDetails = session.createQuery(jpqlFindEmployerDetails, EmployerDetailsResDto.class)
            .setParameter("eid", employerId)
            .getSingleResultOrNull();
        if (isNull(employerDetails)) return empty();
        return of(employerDetails);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Optional<AddEditEmployerReqDto> findEmployerEditPageDetails(Object employerId) {
        final String jpqlFindEmployerBaseId =
            "SELECT new pl.polsl.skirentalservice.dto.employer.AddEditEmployerReqDto(" +
                "d.firstName, d.lastName, d.pesel," +
                "CONCAT(SUBSTRING(d.phoneNumber, 1, 3), ' ', SUBSTRING(d.phoneNumber, 4, 3), ' '," +
                "SUBSTRING(d.phoneNumber, 7, 3)), CAST(d.bornDate AS string), CAST(e.hiredDate AS string)," +
                "a.street, a.buildingNr, a.apartmentNr, a.city, a.postalCode, d.gender" +
            ") FROM EmployerEntity e " +
            "INNER JOIN e.userDetails d INNER JOIN e.locationAddress a INNER JOIN e.role r " +
            "WHERE e.id = :uid";
        final AddEditEmployerReqDto employerDetails = session
            .createQuery(jpqlFindEmployerBaseId, AddEditEmployerReqDto.class)
            .setParameter("uid", employerId)
            .getSingleResultOrNull();
        if (isNull(employerDetails)) return empty();
        return of(employerDetails);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void updateEmployerFirstAccessPassword(String newPassword, Object employerId) {
        final String updateUserPassword =
            "UPDATE EmployerEntity e SET e.password = :newPassword, e.firstAccess = false WHERE e.id = :id";
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
        final String jpqlCheckIfHasAnyRents =
            "SELECT COUNT(r.id) > 0 FROM RentEntity r INNER JOIN r.employer e " +
            "WHERE e.id = :eid AND r.status <> :st";
        return session.createQuery(jpqlCheckIfHasAnyRents, Boolean.class)
            .setParameter("eid", employerId)
            .setParameter("st", RETURNED)
            .getSingleResult();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Long findAllEmployersCount(FilterDataDto filterData) {
        final String jpqlTotalEmployersCount =
            "SELECT COUNT(e.id) FROM EmployerEntity e " +
            "INNER JOIN e.userDetails d INNER JOIN e.role r " +
            "WHERE r.id = 1 AND " + filterData.getSearchColumn() + " LIKE :search";
        return session.createQuery(jpqlTotalEmployersCount, Long.class)
            .setParameter("search", "%" + filterData.getSearchText() + "%")
            .getSingleResult();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public List<OwnerMailPayloadDto> findAllEmployersMailSenders() {
        final String jpqlFindAllOwners =
            "SELECT new pl.polsl.skirentalservice.dto.OwnerMailPayloadDto(" +
                "CONCAT(d.firstName, ' ', d.lastName), d.emailAddress" +
            ") FROM EmployerEntity e " +
            "INNER JOIN e.userDetails d INNER JOIN e.role r WHERE r.alias = 'K'";
        return session
            .createQuery(jpqlFindAllOwners, OwnerMailPayloadDto.class)
            .getResultList();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public List<EmployerRecordResDto> findAllPageableEmployersRecords(
        FilterDataDto filterData, SorterDataDto sorterData, int page, int total
    ) {
        final String jpqlFindAllEmployers =
            "SELECT new pl.polsl.skirentalservice.dto.employer.EmployerRecordResDto(" +
                "e.id, CONCAT(d.firstName, ' ', d.lastName), e.hiredDate, d.pesel, d.emailAddress," +
                "CONCAT('+', d.phoneAreaCode, ' ', SUBSTRING(d.phoneNumber, 1, 3), ' '," +
                "SUBSTRING(d.phoneNumber, 4, 3), ' ', SUBSTRING(d.phoneNumber, 7, 3)), d.gender" +
            ") FROM EmployerEntity e " +
            "INNER JOIN e.userDetails d INNER JOIN e.role r " +
            "WHERE r.id <> 2 AND " + filterData.getSearchColumn() + " LIKE :search " +
            "ORDER BY " + sorterData.getJpql();
        return session
            .createQuery(jpqlFindAllEmployers, EmployerRecordResDto.class)
            .setParameter("search", "%" + filterData.getSearchText() + "%")
            .setFirstResult((page - 1) * total)
            .setMaxResults(total)
            .getResultList();
    }
}
