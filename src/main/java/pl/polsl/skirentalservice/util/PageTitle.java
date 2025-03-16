package pl.polsl.skirentalservice.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pl.polsl.skirentalservice.core.ServerConfigBean;

@Getter
@AllArgsConstructor
public enum PageTitle {
    NOT_FOUND_4O4_PAGE("404"),
    INTERNAL_SERVER_ERROR_PAGE("500"),
    FORBIDDEN_PAGE("403"),
    LOGIN_PAGE("Logowanie"),
    FIRST_ACCESS_PAGE("Inicjalizacja konta"),
    FORGOT_PASSWORD_REQUEST_PAGE("Resetuj hasło"),
    CHANGE_FORGOTTEN_PASSWORD_PAGE("Zmień hasło"),

    OWNER_DASHBOARD_PAGE("Panel kierownika"),
    OWNER_PROFILE_PAGE("Konto kierownika"),
    OWNER_SETTINGS_PAGE("Ustawienia konta kierownika"),
    OWNER_EMPLOYERS_PAGE("Lista pracowników"),
    OWNER_EMPLOYER_DETAILS_PAGE("Szczegóły pracownika"),
    OWNER_ADD_EMPLOYER_PAGE("Dodaj pracownika"),
    OWNER_EDIT_EMPLOYER_PAGE("Edytuj pracownika"),
    OWNER_ADD_EQUIPMENT_PAGE("Dodaj nowy sprzęt"),
    OWNER_EDIT_EQUIPMENT_PAGE("Edytuj sprzęt"),

    SELLER_DASHBOARD_PAGE("Panel pracownika"),
    SELLER_PROFILE_PAGE("Konto pracownika"),
    SELLER_SETTINGS_PAGE("Ustawienia konta pracownika"),
    SELLER_ADD_CUSTOMER_PAGE("Dodaj klienta"),
    SELLER_EDIT_CUSTOMER_PAGE("Edytuj klienta"),
    SELLER_CREATE_NEW_RENT_PAGE("Kreator nowego wypożyczenia"),
    SELLER_GENERATE_RETURN_PAGE("Generuj zwroj wypożyczenia"),

    COMMON_RENTS_PAGE("Lista wypożyczeń"),
    COMMON_RENT_DETAILS_PAGE("Szczegóły wypożyczenia"),
    COMMON_CUSTOMERS_PAGE("Lista klientów"),
    COMMON_CUSTOMER_DETAILS_PAGE("Szczegóły klienta"),
    COMMON_EQUIPMENTS_PAGE("Lista sprzętów narciarskich"),
    COMMON_EQUIPMENT_DETAILS_PAGE("Szczegóły sprzętu narciarskiego"),
    COMMON_RETURNS_PAGE("Lista zwrotów"),
    COMMON_RETURN_DETAILS_PAGE("Szczegóły zwrotu"),
    ;

    private final String name;

    public String getName(ServerConfigBean serverConfigBean) {
        return name + " | " + serverConfigBean.getTitlePageTag();
    }
}
