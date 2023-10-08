/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.paging.filter;

import lombok.Getter;
import pl.polsl.skirentalservice.dto.FormSelectTupleDto;

@Getter
public class FilterColumn extends FormSelectTupleDto {
    private final String columnName;

    public FilterColumn(String value, String text, String columnName) {
        this(false, value, text, columnName);
    }

    public FilterColumn(boolean isSelected, String value, String text, String columnName) {
        super(isSelected, value, text);
        this.columnName = columnName;
    }
}
