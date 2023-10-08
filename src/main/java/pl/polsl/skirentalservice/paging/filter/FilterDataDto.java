/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.paging.filter;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class FilterDataDto {
    private String searchText;
    private String searchColumn;
    private List<FilterColumn> searchBy;

    public FilterDataDto(List<FilterColumn> searchBy) {
        this.searchText = "";
        this.searchBy = searchBy;
        this.searchColumn = searchBy.get(0).getColumnName();
    }
}
