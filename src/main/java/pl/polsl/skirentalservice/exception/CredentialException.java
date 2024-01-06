/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import pl.polsl.skirentalservice.dto.login.LoginFormReqDto;

public class CredentialException {
    public static class PasswordMismatchException extends RuntimeException {
        public PasswordMismatchException(String firstField, String secondField) {
            super("Hasła w polach <strong>" + firstField + "</strong> oraz <strong>" + secondField + "</strong> " +
                "muszą być takie same.");
        }
    }

    public static class OtaTokenNotFoundException extends RuntimeException {
        public OtaTokenNotFoundException(HttpServletRequest req, String token, Logger logger) {
            super("Podany token nie istnieje, został już wykorzystany lub uległ przedawnieniu. Przejdź " +
                "<a class='alert-link' href='" + req.getContextPath() + "/forgot-password-request'>tutaj</a>, " +
                "aby wygenerować nowy token.");
            logger.warn("Attempt to change password with non-existing, expired or already used token. Token: {}", token);
        }
    }

    public static class InvalidCredentialsException extends RuntimeException {
        public InvalidCredentialsException(LoginFormReqDto reqDto, Logger logger) {
            super("Nieprawidłowe hasło. Spróbuj ponownie podając inne hasło.");
            logger.warn("Attempt to login with invalid credentials. Login/email data: {}", reqDto.getLoginOrEmail());
        }
    }
}
