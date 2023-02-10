/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 * Silesian University of Technology
 *
 *  File name: AlertType.java
 *  Last modified: 20/01/2023, 05:19
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
public enum AlertType {
    INFO("alert-success"),
    WARN("alert-warning"),
    ERROR("alert-danger");

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private final String cssClass;
}
