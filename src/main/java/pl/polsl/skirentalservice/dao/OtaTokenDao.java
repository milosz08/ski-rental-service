package pl.polsl.skirentalservice.dao;

import pl.polsl.skirentalservice.dto.change_password.ChangePasswordEmployerDetailsDto;
import pl.polsl.skirentalservice.dto.change_password.TokenDetailsDto;

import java.util.Optional;

public interface OtaTokenDao {
    Optional<ChangePasswordEmployerDetailsDto> findTokenRelatedToEmployer(String token);

    Optional<TokenDetailsDto> findTokenDetails(String token);

    void manuallyExpiredOtaToken(Object id);

    boolean checkIfTokenExist(String token);
}
