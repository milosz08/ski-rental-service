/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: OtaTokenDao.java
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

package pl.polsl.skirentalservice.dao.ota_token;

import lombok.RequiredArgsConstructor;

import org.hibernate.Session;

import java.util.Objects;
import java.util.Optional;

import pl.polsl.skirentalservice.dto.change_password.TokenDetailsDto;
import pl.polsl.skirentalservice.dto.change_password.ChangePasswordEmployerDetailsDto;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@RequiredArgsConstructor
public class OtaTokenDao implements IOtaTokenDao {

    private final Session session;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Optional<ChangePasswordEmployerDetailsDto> findTokenRelatedToEmployer(String token) {
        final String jpqlFindToken = """
            SELECT new pl.polsl.skirentalservice.dto.change_password.ChangePasswordEmployerDetailsDto(
                e.id, t.id, CONCAT(d.firstName, ' ', d.lastName)
            ) FROM OtaTokenEntity t
            INNER JOIN t.employer e
            INNER JOIN e.userDetails d
            WHERE t.otaToken = :token AND t.isUsed = false AND t.expiredDate >= NOW()
        """;
        final var details = session.createQuery(jpqlFindToken, ChangePasswordEmployerDetailsDto.class)
            .setParameter("token", token)
            .getSingleResultOrNull();
        if (Objects.isNull(details)) return Optional.empty();
        return Optional.of(details);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Optional<TokenDetailsDto> findTokenDetails(String token) {
        final String jpqlFindToken = """
            SELECT new pl.polsl.skirentalservice.dto.change_password.TokenDetailsDto(e.id, t.id)
            FROM OtaTokenEntity t INNER JOIN t.employer e
            WHERE t.otaToken = :token AND t.isUsed = false AND t.expiredDate >= NOW()
        """;
        final var details = session.createQuery(jpqlFindToken, TokenDetailsDto.class)
            .setParameter("token", token)
            .getSingleResultOrNull();
        if (Objects.isNull(details)) return Optional.empty();
        return Optional.of(details);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void manuallyExpiredOtaToken(Object id) {
        session.createMutationQuery("UPDATE OtaTokenEntity t SET t.isUsed = true WHERE t.id = :tokenId")
            .setParameter("tokenId", id)
            .executeUpdate();
    }
}
