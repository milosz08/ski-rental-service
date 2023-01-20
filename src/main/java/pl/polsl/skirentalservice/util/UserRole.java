/*
 * Copyright (c) 2022 by multiple authors
 * Silesian University of Technology
 *
 *  File name: UserRole.java
 *  Last modified: 28/12/2022, 01:55
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
public enum UserRole {
    USER("użytkownik", 'U'),
    SELLER("pracownik", 'P'),
    OWNER("kierownik", 'K');

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private final String name;
    private final Character alias;
    private final String eng;
}
