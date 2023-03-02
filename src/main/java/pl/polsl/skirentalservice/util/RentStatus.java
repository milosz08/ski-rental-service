/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 * Silesian University of Technology
 *
 *  File name: RentStatus.java
 *  Last modified: 31/01/2023, 04:49
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
public enum RentStatus {
    OPENED      ("otwarty", "opened"),
    RENTED      ("wypożyczony", "rented"),
    RETURNED    ("zwrócony", "returned");
    
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private final String status;
    private final String statusEng;
}
