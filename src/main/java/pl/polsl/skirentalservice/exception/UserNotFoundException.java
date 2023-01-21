/*
 * Copyright (c) 2023 by multiple authors
 * Silesian University of Technology
 *
 *  File name: UserNotFoundException.java
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
import pl.polsl.skirentalservice.dto.change_password.RequestToChangePasswordReqDto;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public class UserNotFoundException extends RuntimeException {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserNotFoundException.class);

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public UserNotFoundException(String userId) {
        super("Użytkownik z podanym ID <strong>#" + userId + "</strong> nie istnieje w systemie.");
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public UserNotFoundException(RequestToChangePasswordReqDto reqDto) {
        super("Użytkownik z podanymi danymi logowania nie istnieje w systemie.");
        LOGGER.warn("Attempt to change password for non existing account. Login data: {}", reqDto);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public UserNotFoundException(LoginFormReqDto reqDto) {
        super("Użytkownik z podanymi danymi logowania nie istnieje w systemie.");
        LOGGER.warn("Attempt to login on non existing account. Login data: {}", reqDto);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public UserNotFoundException(Long userId) {
        super("Użytkownik z podanym ID <strong>#" + userId + "</strong> nie istnieje w systemie.");
    }
}
