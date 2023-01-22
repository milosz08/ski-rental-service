/*
 * Copyright (c) 2023 by multiple authors
 * Silesian University of Technology
 *
 *  File name: InvalidCredentialsException.java
 *  Last modified: 21/01/2023, 16:18
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.exception;

import org.slf4j.*;
import pl.polsl.skirentalservice.dto.login.LoginFormReqDto;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public class InvalidCredentialsException extends RuntimeException {

    private static final Logger LOGGER = LoggerFactory.getLogger(InvalidCredentialsException.class);

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public InvalidCredentialsException(LoginFormReqDto reqDto) {
        super("Nieprawidłowe hasło. Spróbuj ponownie podając inne hasło.");
        LOGGER.warn("Attempt to login with invalid credentials. Credentials data: {}", reqDto);
    }
}