/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import pl.polsl.skirentalservice.dto.login.LoginFormReqDto;

public class CredentialException {
    public static class PasswordMismatchException extends RuntimeException {
        public PasswordMismatchException(String firstField, String secondField) {
            super("Hasła w polach <strong>" + firstField + "</strong> oraz <strong>" + secondField + "</strong> " +
                "muszą być takie same.");
        }
    }

    @Slf4j
    public static class OtaTokenNotFoundException extends RuntimeException {
        public OtaTokenNotFoundException(HttpServletRequest req, String token) {
            super("Podany token nie istnieje, został już wykorzystany lub uległ przedawnieniu. Przejdź " +
                "<a class='alert-link' href='" + req.getContextPath() + "/forgot-password-request'>tutaj</a>, " +
                "aby wygenerować nowy token.");
            log.warn("Attempt to change password with non-existing, expired or already used token. Token: {}", token);
        }
    }

    @Slf4j
    public static class InvalidCredentialsException extends RuntimeException {
        public InvalidCredentialsException(LoginFormReqDto reqDto) {
            super("Nieprawidłowe hasło. Spróbuj ponownie podając inne hasło.");
            log.warn("Attempt to login with invalid credentials. Login/email data: {}", reqDto.getLoginOrEmail());
        }
    }
}
