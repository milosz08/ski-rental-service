/*
 * Copyright (c) 2022 by multiple authors
 * Silesian University of Technology
 *
 *  File name: PageTitle.java
 *  Last modified: 22.12.2022, 17:51
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.util;

import lombok.*;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Getter
@AllArgsConstructor
public enum PageTitle {
    NOT_FOUND_4O4_PAGE("404"),
    LOGIN_PAGE("Logowanie"),
    FORGOT_PASSWORD_REQUEST_PAGE("Resetuj hasło"),
    CHANGE_FORGOTTEN_PASSWORD_PAGE("Zmień hasło"),

    OWNER_DASHBOARD_PAGE("Panel kierownika"),
    OWNER_PROFILE_PAGE("Konto kierownika"),
    OWNER_SETTINGS_PAGE("Ustawienia konta kierownika"),

    SELLER_DASHBOARD_PAGE("Panel pracownika"),
    SELLER_PROFILE_PAGE("Konto pracownika"),
    SELLER_SETTINGS_PAGE("Ustawienia konta pracownika");

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private final String name;
}
