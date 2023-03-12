/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 * Silesian University of Technology
 *
 *  File name: SortDirection.java
 *  Last modified: 22/01/2023, 16:07
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.paging.sorter;

import lombok.Getter;
import lombok.AllArgsConstructor;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Getter
@AllArgsConstructor
public enum SortDirection {
    IDLE("IDLE"),
    ASC("ASC"),
    DESC("DESC");

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private final String dir;
}
