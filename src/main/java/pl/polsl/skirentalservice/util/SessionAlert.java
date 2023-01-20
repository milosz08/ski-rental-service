/*
 * Copyright (c) 2023 by multiple authors
 * Silesian University of Technology
 *
 *  File name: SessionAlert.java
 *  Last modified: 17/01/2023, 21:46
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
public enum SessionAlert {
    LOGIN_PAGE_ALERT("login-page-alert"),
    EMPLOYERS_PAGE_ALERT("employers-page-alert");

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private final String name;
}
