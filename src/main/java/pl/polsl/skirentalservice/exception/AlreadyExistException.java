/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import pl.polsl.skirentalservice.util.UserRole;

public class AlreadyExistException {

    public static class PeselAlreadyExistException extends RuntimeException {
        public PeselAlreadyExistException(String pesel, UserRole role) {
            super(StringUtils.capitalize(role.getName()) + " z numerem PESEL <strong>" + pesel +
                "</strong> istnieje już w systemie.");
        }
    }

    public static class PhoneNumberAlreadyExistException extends RuntimeException {
        public PhoneNumberAlreadyExistException(String phoneNumber, UserRole role) {
            super(StringUtils.capitalize(role.getName()) + " z numerem telefonu <strong>" + phoneNumber +
                "</strong> istnieje już w systemie.");
        }
    }

    public static class EmailAddressAlreadyExistException extends RuntimeException {
        public EmailAddressAlreadyExistException(String emailAddress, UserRole role) {
            super(StringUtils.capitalize(role.getName()) + " z adresem email <strong>" + emailAddress +
                "</strong> istnieje już w systemie.");
        }
    }

    public static class EquipmentTypeAlreadyExistException extends RuntimeException {
        public EquipmentTypeAlreadyExistException() {
            super("Wybrany typ sprzętu narciarskiego do dodania istnieje już w systemie. Spróbuj dodać inny sprzęt, " +
                "bądź wybierz z listy dostępnych.");
        }
    }

    public static class EquipmentBrandAlreadyExistException extends RuntimeException {
        public EquipmentBrandAlreadyExistException() {
            super("Wybrana marka sprzętu narciarskiego do dodania istnieje już w systemie. Spróbuj dodać inną markę, " +
                "bądź wybierz z listy dostępnych.");
        }
    }

    public static class EquipmentColorAlreadyExistException extends RuntimeException {
        public EquipmentColorAlreadyExistException() {
            super("Wybrany kolor sprzętu narciarskiego do dodania istnieje już w systemie. Spróbuj dodać inny kolor, " +
                "bądź wybierz z listy dostępnych.");
        }
    }

    public static class EquipmentAlreadyExistException extends RuntimeException {
        public EquipmentAlreadyExistException() {
            super("Wybrany model sprzętu istnieje już w systemie Jeśli chcesz dodać więcej elementów, zwiększ ich ilość" +
                "w wypożyczalni.");
        }
    }

    public static class EquipmentTypeHasConnectionsException extends RuntimeException {
        public EquipmentTypeHasConnectionsException() {
            super("Wybrany typ sprzętu narciarskiego nie jest możliwy do usunięcia, ponieważ powiązane są z nim sprzęty. " +
                "Jeśli chcesz usunąć typ, musisz usunąć wszystkie powiązane z nim sprzęty.");
        }
    }

    public static class EquipmentBrandHasConnectionsException extends RuntimeException {
        public EquipmentBrandHasConnectionsException() {
            super("Wybrana marka sprzętu narciarskiego nie jest możliwa do usunięcia, ponieważ powiązane są z nią sprzęty. " +
                "Jeśli chcesz usunąć markę, musisz usunąć wszystkie powiązane z nią sprzęty.");
        }
    }

    public static class EquipmentColorHasConnectionsException extends RuntimeException {
        public EquipmentColorHasConnectionsException() {
            super("Wybrany kolor sprzętu narciarskiego nie jest możliwy do usunięcia, ponieważ powiązane są z nim sprzęty. " +
                "Jeśli chcesz usunąć kolor, musisz usunąć wszystkie powiązane z nim sprzęty.");
        }
    }

    public static class EquipmentInCartAlreadyExistException extends RuntimeException {
        public EquipmentInCartAlreadyExistException() {
            super("Dodawany sprzęt istnieje już w zestawieniu nowego wypożyczenia. Aby zwiększyć ilość, edytuj pozycję.");
        }
    }

    public static class TooMuchEquipmentsException extends RuntimeException {
        public TooMuchEquipmentsException() {
            super("Podano więcej sztuk sprzętu, aniżeli jest dostępnych na stanie.");
        }
    }

    public static class CustomerHasOpenedRentsException extends RuntimeException {
        public CustomerHasOpenedRentsException() {
            super("Nie jest możliwe usunięcie użytkownika, który posiada w danej chwili przynamniej jedno wypożyczenie " +
                "ze statusem innym niż <strong>Zwrócone</strong>.");
        }
    }

    public static class EmployerHasOpenedRentsException extends RuntimeException {
        public EmployerHasOpenedRentsException() {
            super("Nie jest możliwe usunięcie pracownika, który zarządza w danej chwili przynamniej jednym wypożyczeniem " +
                "ze statusem innym niż <strong>Zwrócone</strong>.");
        }
    }

    public static class EquipmenHasOpenedRentsException extends RuntimeException {
        public EquipmenHasOpenedRentsException() {
            super("Nie jest możliwe usunięcie sprzętu narciarskiego, który obecny jest w danej chwili w przynajmniej " +
                "jednym wypożyczeniu ze statusem innym niż <strong>Zwrócone</strong>.");
        }
    }

    public static class ReturnDocumentAlreadyExistException extends RuntimeException {
        public ReturnDocumentAlreadyExistException(HttpServletRequest req, String returnIdentifier, Long returnId) {
            super("Wybrane wypożyczenie posiada już wygenerowany dokument zwrotu. Możesz go znaleźć, klikając w " +
                "<a class='alert-link' href='" + req.getContextPath() + "/seller/return-details?id=" + returnId +
                "'>ten link</a> lub wyszukujac po numerze zwrotu: <strong>" + returnIdentifier + "</strong>.");
        }
    }
}
