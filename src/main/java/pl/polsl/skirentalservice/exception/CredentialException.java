/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: CredentialException.java
 * Last modified: 3/12/23, 11:01 AM
 * Project name: ski-rental-service
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 *
 *     <http://www.apache.org/license/LICENSE-2.0>
 *
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the license.
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
