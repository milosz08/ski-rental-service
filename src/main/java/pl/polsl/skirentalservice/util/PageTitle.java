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

//----------------------------------------------------------------------------------------------------------------------

@Getter
@AllArgsConstructor
public enum PageTitle {
    START_PAGE("Witamy"),
    HELLO_PAGE("Testowy servlet");

    //------------------------------------------------------------------------------------------------------------------

    private final String name;
}
