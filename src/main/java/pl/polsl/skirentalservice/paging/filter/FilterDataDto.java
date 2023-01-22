/*
 * Copyright (c) 2023 by multiple authors
 * Silesian University of Technology
 *
 *  File name: FilterDataDto.java
 *  Last modified: 22/01/2023, 16:06
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.paging.filter;

import lombok.*;
import java.util.List;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Data
@AllArgsConstructor
public class FilterDataDto {
    private String searchText;
    private String searchColumn;
    private List<FilterColumn> searchBy;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public FilterDataDto(List<FilterColumn> searchBy) {
        this.searchText = "";
        this.searchBy = searchBy;
        this.searchColumn = searchBy.get(0).getColumnName();
    }
}
