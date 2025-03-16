package pl.polsl.skirentalservice.exception;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import pl.polsl.skirentalservice.core.AbstractAppException;
import pl.polsl.skirentalservice.util.UserRole;

public class NotFoundException {
    @Slf4j
    public static class UserNotFoundException extends AbstractAppException {
        public UserNotFoundException(Object userId) {
            super("Użytkownik z podanym ID <strong>#" + userId + "</strong> nie istnieje w systemie.");
        }

        public UserNotFoundException(UserRole role) {
            super(StringUtils.capitalize(role.getName()) + " nie istnieje w systemie.");
        }
    }

    public static class EquipmentTypeNotFoundException extends AbstractAppException {
        public EquipmentTypeNotFoundException() {
            super("Wybrany typ sprzętu narciarskiego nie istnieje w systemie.");
        }
    }

    public static class EquipmentBrandNotFoundException extends AbstractAppException {
        public EquipmentBrandNotFoundException() {
            super("Wybrana marka sprzętu narciarskiego nie istnieje w systemie.");
        }
    }

    public static class EquipmentColorNotFoundException extends AbstractAppException {
        public EquipmentColorNotFoundException() {
            super("Wybrany kolor sprzętu narciarskiego nie istnieje w systemie.");
        }
    }

    public static class EquipmentNotFoundException extends AbstractAppException {
        public EquipmentNotFoundException(Object eqId) {
            super("Wybrany sprzęt narciarski na podstawie ID <strong>#" + eqId + "</strong> nie istnieje w systemie.");
        }

        public EquipmentNotFoundException() {
            super("Nie znaleziono żadnych sprzętów narciarskich w wyszukiwanym zapytaniu.");
        }
    }

    public static class EquipmentInCartNotFoundException extends AbstractAppException {
        public EquipmentInCartNotFoundException() {
            super("Wybrany sprzęt na podstawie identyfikatora nie istnieje na liście z zestawieniami.");
        }
    }

    public static class AnyEquipmentsInCartNotFoundException extends AbstractAppException {
        public AnyEquipmentsInCartNotFoundException() {
            super("W celu złożenia nowego wypożyczenia należy dodać do niego przynajmniej jeden sprzęt.");
        }
    }

    public static class RentNotFoundException extends AbstractAppException {
        public RentNotFoundException() {
            super("Szukane wypożyczenie nie istnieje w systemie, bądź zostało z niego usunięte.");
        }
    }

    public static class ReturnNotFoundException extends AbstractAppException {
        public ReturnNotFoundException() {
            super("Szukany zwrot wypożyczenia nie istnieje w systemie, bądź zostało z niego usunięty.");
        }
    }
}
