/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 * Silesian University of Technology
 *
 *  File name: ServletSorterField.java
 *  Last modified: 22/01/2023, 16:07
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.paging.sorter;

import lombok.Getter;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Getter
public class ServletSorterField {
    private SortDirection direction;
    private String chevronBts;
    private final String jpql;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public ServletSorterField(String jpql) {
        this.jpql = jpql;
        this.direction = SortDirection.IDLE;
        this.chevronBts = "down-up text-secondary";
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    String setAscending() {
        direction = SortDirection.ASC;
        chevronBts = "down";
        return jpql + " ASC";
    }

    String setDescending() {
        direction = SortDirection.DESC;
        chevronBts = "up";
        return jpql + " DESC";
    }

    String reset(String defColumn) {
        direction = SortDirection.IDLE;
        chevronBts = "down-up text-secondary";
        return defColumn + " ASC";
    }
}
