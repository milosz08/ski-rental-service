/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 * Silesian University of Technology
 *
 *  File name: OtaTokenDao.java
 *  Last modified: 20/02/2023, 17:28
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.dao.ota_token;

import lombok.RequiredArgsConstructor;

import java.util.*;
import org.hibernate.Session;

import pl.polsl.skirentalservice.dto.change_password.*;

import static java.util.Optional.*;
import static java.util.Objects.isNull;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@RequiredArgsConstructor
public class OtaTokenDao implements IOtaTokenDao {

    private final Session session;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Optional<ChangePasswordEmployerDetailsDto> findTokenRelatedToEmployer(String token) {
        final String jpqlFindToken =
            "SELECT new pl.polsl.skirentalservice.dto.change_password.ChangePasswordEmployerDetailsDto(" +
                "e.id, t.id, CONCAT(d.firstName, ' ', d.lastName)" +
            ") FROM OtaTokenEntity t " +
            "INNER JOIN t.employer e " +
            "INNER JOIN e.userDetails d " +
            "WHERE t.otaToken = :token AND t.isUsed = false AND t.expiredDate >= NOW()";
        final var details = session.createQuery(jpqlFindToken, ChangePasswordEmployerDetailsDto.class)
            .setParameter("token", token)
            .getSingleResultOrNull();
        if (isNull(details)) return empty();
        return of(details);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Optional<TokenDetailsDto> findTokenDetails(String token) {
        final String jpqlFindToken =
            "SELECT new pl.polsl.skirentalservice.dto.change_password.TokenDetailsDto(e.id, t.id) " +
            "FROM OtaTokenEntity t INNER JOIN t.employer e " +
            "WHERE t.otaToken = :token AND t.isUsed = false AND t.expiredDate >= NOW()";
        final var details = session.createQuery(jpqlFindToken, TokenDetailsDto.class)
            .setParameter("token", token)
            .getSingleResultOrNull();
        if (isNull(details)) return empty();
        return of(details);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void manuallyExpiredOtaToken(Object id) {
        session.createMutationQuery("UPDATE OtaTokenEntity t SET t.isUsed = true WHERE t.id = :tokenId")
            .setParameter("tokenId", id)
            .executeUpdate();
    }
}
