package pl.polsl.skirentalservice.dao.hibernate;

import org.hibernate.Session;
import pl.polsl.skirentalservice.dao.OtaTokenDao;
import pl.polsl.skirentalservice.dao.core.AbstractHibernateDao;
import pl.polsl.skirentalservice.dto.change_password.ChangePasswordEmployerDetailsDto;
import pl.polsl.skirentalservice.dto.change_password.TokenDetailsDto;

import java.util.Optional;

public class OtaTokenDaoHib extends AbstractHibernateDao implements OtaTokenDao {
    public OtaTokenDaoHib(Session session) {
        super(session);
    }

    @Override
    public Optional<ChangePasswordEmployerDetailsDto> findTokenRelatedToEmployer(String token) {
        final String jpqlFindToken = """
                SELECT new pl.polsl.skirentalservice.dto.change_password.ChangePasswordEmployerDetailsDto(
                    e.id, t.id, CONCAT(d.firstName, ' ', d.lastName)
                ) FROM OtaTokenEntity t
                INNER JOIN t.employer e
                INNER JOIN e.userDetails d
                WHERE t.otaToken = :token AND t.isUsed = false AND t.expiredAt >= NOW()
            """;
        final var details = session.createQuery(jpqlFindToken, ChangePasswordEmployerDetailsDto.class)
            .setParameter("token", token)
            .getSingleResultOrNull();
        return Optional.ofNullable(details);
    }

    @Override
    public Optional<TokenDetailsDto> findTokenDetails(String token) {
        final String jpqlFindToken = """
                SELECT new pl.polsl.skirentalservice.dto.change_password.TokenDetailsDto(e.id, t.id)
                FROM OtaTokenEntity t INNER JOIN t.employer e
                WHERE t.otaToken = :token AND t.isUsed = false AND t.expiredAt >= NOW()
            """;
        final var details = session.createQuery(jpqlFindToken, TokenDetailsDto.class)
            .setParameter("token", token)
            .getSingleResultOrNull();
        return Optional.ofNullable(details);
    }

    @Override
    public void manuallyExpiredOtaToken(Object id) {
        session.createMutationQuery("UPDATE OtaTokenEntity t SET t.isUsed = true WHERE t.id = :tokenId")
            .setParameter("tokenId", id)
            .executeUpdate();
    }

    @Override
    public boolean checkIfTokenExist(String token) {
        final String jpqlCheckToken = "SELECT COUNT(t.id) > 0 FROM OtaTokenEntity t WHERE t.otaToken = :token";
        return session.createQuery(jpqlCheckToken, Boolean.class)
            .setParameter("token", token)
            .getSingleResultOrNull();
    }
}
