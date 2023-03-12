/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 * Silesian University of Technology
 *
 *  File name: UserRole.java
 *  Last modified: 31/01/2023, 03:45
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.util;

import lombok.Getter;
import lombok.AllArgsConstructor;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Getter
@AllArgsConstructor
public enum UserRole {
    USER        ("użytkownik", 'U', "user"),
    SELLER      ("pracownik", 'P', "seller"),
    OWNER       ("kierownik", 'K', "owner");

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private final String name;
    private final Character alias;
    private final String eng;
}
