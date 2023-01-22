/*
 * Copyright (c) 2023 by multiple authors
 * Silesian University of Technology
 *
 *  File name: SearchContentDto.java
 *  Last modified: 22/01/2023, 03:52
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.dto.search_filter;

import lombok.*;
import jakarta.servlet.http.HttpServletRequest;

import java.util.*;

import static org.apache.commons.lang3.StringUtils.trimToEmpty;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Data
@AllArgsConstructor
public class SearchFilterDto {
    private String searchText;
    private String searchColumn;
    private List<SearchBySelectColumn> searchBy;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public SearchFilterDto(List<SearchBySelectColumn> searchBy) {
        this.searchText = "";
        for (final SearchBySelectColumn select : searchBy) {
            if (select.getIsSelected().equals("selected")) {
                this.searchColumn = select.getColumnName();
                break;
            }
        }
        this.searchBy = searchBy;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public SearchFilterDto(HttpServletRequest req, List<SearchBySelectColumn> searchBy) {
        this.searchText = trimToEmpty(req.getParameter("searchText"));
        final String searchByValue = trimToEmpty(req.getParameter("searchBy"));
        for (final SearchBySelectColumn select : searchBy) {
            if (select.getValue().equals(searchByValue)) {
                select.setIsSelected("selected");
                this.searchColumn = select.getColumnName();
            } else {
                select.setIsSelected("");
            }
        }
        this.searchBy = searchBy;
    }
}

