/*
 * Copyright (c) 2023 by multiple authors
 * Silesian University of Technology
 *
 *  File name: AlreadyExistException.java
 *  Last modified: 22/01/2023, 09:59
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.exception;

import pl.polsl.skirentalservice.util.UserRole;
import static org.apache.commons.lang3.StringUtils.capitalize;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public class AlreadyExistException {

    public static class PeselAlreadyExistException extends RuntimeException {
        public PeselAlreadyExistException(String pesel, UserRole role) {
            super(capitalize(role.getName()) + " z numerem PESEL <strong>" + pesel + "</strong> istnieje już w systemie.");
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static class PhoneNumberAlreadyExistException extends RuntimeException {
        public PhoneNumberAlreadyExistException(String phoneNumber, UserRole role) {
            super(capitalize(role.getName()) + " z numerem telefonu <strong>" + phoneNumber + "</strong> istnieje " +
                "już w systemie.");
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static class EmailAddressAlreadyExistException extends RuntimeException {
        public EmailAddressAlreadyExistException(String emailAddress, UserRole role) {
            super(capitalize(role.getName()) + " z adresem email <strong>" + emailAddress + "</strong> istnieje " +
                "już w systemie.");
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static class EquipmentTypeAlreadyExistException extends RuntimeException {
        public EquipmentTypeAlreadyExistException() {
            super("Wybrany typ sprzętu narciarskiego do dodania istnieje już w systemie. Spróbuj dodać inny sprzęt, " +
                "bądź wybierz z listy dostępnych.");
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static class EquipmentBrandAlreadyExistException extends RuntimeException {
        public EquipmentBrandAlreadyExistException() {
            super("Wybrana marka sprzętu narciarskiego do dodania istnieje już w systemie. Spróbuj dodać inną markę, " +
                "bądź wybierz z listy dostępnych.");
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static class EquipmentColorAlreadyExistException extends RuntimeException {
        public EquipmentColorAlreadyExistException() {
            super("Wybrany kolor sprzętu narciarskiego do dodania istnieje już w systemie. Spróbuj dodać inny kolor, " +
                "bądź wybierz z listy dostępnych.");
        }
    }
}
