/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.dao.ota_token;

import pl.polsl.skirentalservice.dto.change_password.ChangePasswordEmployerDetailsDto;
import pl.polsl.skirentalservice.dto.change_password.TokenDetailsDto;

import java.util.Optional;

public interface IOtaTokenDao {
    Optional<ChangePasswordEmployerDetailsDto> findTokenRelatedToEmployer(String token);
    Optional<TokenDetailsDto> findTokenDetails(String token);

    void manuallyExpiredOtaToken(Object id);
}
