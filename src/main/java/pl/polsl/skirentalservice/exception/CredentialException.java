/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 * Silesian University of Technology
 *
 *  File name: CredentialException.java
 *  Last modified: 27/01/2023, 14:43
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.exception;

import org.slf4j.Logger;

import jakarta.servlet.http.HttpServletRequest;

import pl.polsl.skirentalservice.dto.login.LoginFormReqDto;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public class CredentialException {

    public static class PasswordMismatchException extends RuntimeException {
        public PasswordMismatchException(String firstField, String secondField) {
            super("Hasła w polach <strong>" + firstField + "</strong> oraz <strong>" + secondField + "</strong> " +
                "muszą być takie same.");
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static class OtaTokenNotFoundException extends RuntimeException {
        public OtaTokenNotFoundException(HttpServletRequest req, String token, Logger logger) {
            super("Podany token nie istnieje, został już wykorzystany lub uległ przedawnieniu. Przejdź " +
                "<a class='alert-link' href='" + req.getContextPath() + "/forgot-password-request'>tutaj</a>, " +
                "aby wygenerować nowy token.");
            logger.warn("Attempt to change password with non-existing, expired or already used token. Token: {}", token);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static class InvalidCredentialsException extends RuntimeException {
        public InvalidCredentialsException(LoginFormReqDto reqDto, Logger logger) {
            super("Nieprawidłowe hasło. Spróbuj ponownie podając inne hasło.");
            logger.warn("Attempt to login with invalid credentials. Login/email data: {}", reqDto.getLoginOrEmail());
        }
    }
}
