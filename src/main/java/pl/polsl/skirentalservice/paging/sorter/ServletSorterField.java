/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.paging.sorter;

import lombok.Getter;

@Getter
public class ServletSorterField {
    private SortDirection direction;
    private String chevronBts;
    private final String jpql;

    public ServletSorterField(String jpql) {
        this.jpql = jpql;
        this.direction = SortDirection.IDLE;
        this.chevronBts = "down-up text-secondary";
    }

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
