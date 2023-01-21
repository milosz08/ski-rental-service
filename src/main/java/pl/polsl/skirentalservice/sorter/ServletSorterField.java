/*
 * Copyright (c) 2023 by multiple authors
 * Silesian University of Technology
 *
 *  File name: ServletSorterField.java
 *  Last modified: 21/01/2023, 16:18
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.sorter;

import lombok.*;

import static pl.polsl.skirentalservice.sorter.SortDirection.*;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Data
@Getter
@Setter
public class ServletSorterField {
    private SortDirection direction;
    private String chevronBts;
    private boolean isActive;
    private String jpql;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public ServletSorterField(String jpql) {
        this.jpql = jpql;
        this.direction = ASC;
        this.isActive = false;
        this.chevronBts = "";
    }
}
