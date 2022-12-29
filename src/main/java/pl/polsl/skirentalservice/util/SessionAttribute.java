/*
 * Copyright (c) 2022 by multiple authors
 * Silesian University of Technology
 *
 *  File name: SessionAttribute.java
 *  Last modified: 28/12/2022, 07:05
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.util;

import lombok.*;

//----------------------------------------------------------------------------------------------------------------------

@Getter
@AllArgsConstructor
public enum SessionAttribute {
    LOGGED_USER_DETAILS("loggedUserDetails"),
    LOGOUT_MODAL("logoutModal");

    //------------------------------------------------------------------------------------------------------------------

    private final String name;
}
