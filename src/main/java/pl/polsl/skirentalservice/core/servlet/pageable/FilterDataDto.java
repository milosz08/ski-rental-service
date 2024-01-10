/*
 * Copyright (c) 2024 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.core.servlet.pageable;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

@Data
@AllArgsConstructor
public class FilterDataDto {
    private String searchText;
    private String searchColumn;
    private List<FilterColumn> searchBy;

    public FilterDataDto(List<FilterColumn> searchBy) {
        this.searchText = StringUtils.EMPTY;
        this.searchBy = searchBy;
        this.searchColumn = searchBy.get(0).getColumnName();
    }
}
