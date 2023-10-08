/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.dao.ota_token;

import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import pl.polsl.skirentalservice.dto.change_password.ChangePasswordEmployerDetailsDto;
import pl.polsl.skirentalservice.dto.change_password.TokenDetailsDto;

import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
public class OtaTokenDao implements IOtaTokenDao {

    private final Session session;

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

    @Override
    public void manuallyExpiredOtaToken(Object id) {
        session.createMutationQuery("UPDATE OtaTokenEntity t SET t.isUsed = true WHERE t.id = :tokenId")
            .setParameter("tokenId", id)
            .executeUpdate();
    }
}
