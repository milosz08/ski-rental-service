/*
 * Copyright (c) 2023 by multiple authors
 * Silesian University of Technology
 *
 *  File name: NotFoundException.java
 *  Last modified: 22/01/2023, 10:08
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.exception;

import org.slf4j.Logger;

import pl.polsl.skirentalservice.dto.login.LoginFormReqDto;
import pl.polsl.skirentalservice.dto.change_password.RequestToChangePasswordReqDto;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public class NotFoundException {

    public static class UserNotFoundException extends RuntimeException {
        public UserNotFoundException(String userId) {
            super("Użytkownik z podanym ID <strong>#" + userId + "</strong> nie istnieje w systemie.");
        }

        public UserNotFoundException(RequestToChangePasswordReqDto reqDto, Logger logger) {
            super("Użytkownik z podanymi danymi logowania nie istnieje w systemie.");
            logger.warn("Attempt to change password for non existing account. Login data: {}", reqDto);
        }

        public UserNotFoundException(LoginFormReqDto reqDto, Logger logger) {
            super("Użytkownik z podanymi danymi logowania nie istnieje w systemie.");
            logger.warn("Attempt to login on non existing account. Login data: {}", reqDto);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static class EquipmentTypeNotFoundException extends RuntimeException {
        public EquipmentTypeNotFoundException() {
            super("Wybrany typ sprzętu narciarskiego nie istnieje w systemie.");
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static class EquipmentBrandNotFoundException extends RuntimeException {
        public EquipmentBrandNotFoundException() {
            super("Wybrana marka sprzętu narciarskiego nie istnieje w systemie.");
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static class EquipmentColorNotFoundException extends RuntimeException {
        public EquipmentColorNotFoundException() {
            super("Wybrany kolor sprzętu narciarskiego nie istnieje w systemie.");
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static class EquipmentNotFoundException extends RuntimeException {
        public EquipmentNotFoundException(String eqId) {
            super("Wybrany sprzęt narciarski na podstawie ID <strong>#" + eqId + "</strong> nie istnieje w systemie.");
        }
    }
}
