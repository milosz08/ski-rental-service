/*
 * Copyright (c) 2023 by multiple authors
 * Silesian University of Technology
 *
 *  File name: FilterColumn.java
 *  Last modified: 22/01/2023, 16:06
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.paging.filter;

import lombok.Getter;

import pl.polsl.skirentalservice.dto.FormSelectTupleDto;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Getter
public class FilterColumn extends FormSelectTupleDto {
    private final String columnName;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public FilterColumn(String value, String text, String columnName) {
        this(false, value, text, columnName);
    }

    public FilterColumn(boolean isSelected, String value, String text, String columnName) {
        super(isSelected, value, text);
        this.columnName = columnName;
    }
}
