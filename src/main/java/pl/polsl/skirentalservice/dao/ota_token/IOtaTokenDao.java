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

import java.util.Optional;
import pl.polsl.skirentalservice.dto.change_password.*;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public interface IOtaTokenDao {
    Optional<ChangePasswordEmployerDetailsDto> findTokenRelatedToEmployer(String token);
    Optional<TokenDetailsDto> findTokenDetails(String token);

    void manuallyExpiredOtaToken(Object id);
}
