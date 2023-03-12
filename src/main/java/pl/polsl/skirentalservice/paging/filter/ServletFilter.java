/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 * Silesian University of Technology
 *
 *  File name: ServletFilter.java
 *  Last modified: 22/01/2023, 16:07
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.paging.filter;

import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import pl.polsl.skirentalservice.util.SessionAttribute;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public class ServletFilter {

    private final HttpServletRequest req;
    private final List<FilterColumn> fields;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public ServletFilter(HttpServletRequest req, List<FilterColumn> fields) {
        this.req = req;
        this.fields = fields;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public FilterDataDto generateFilterJPQuery(SessionAttribute attribute) {
        final HttpSession session = req.getSession();
        FilterDataDto filterData = (FilterDataDto) session.getAttribute(attribute.getName());
        if (Objects.isNull(filterData)) filterData = new FilterDataDto(fields);

        final String searchText = StringUtils.trimToEmpty(req.getParameter("searchText"));
        final String searchByValue = req.getParameter("searchBy");
        if (Objects.isNull(searchByValue)) return filterData;

        for (final FilterColumn select : filterData.getSearchBy()) {
            if (select.getValue().equals(searchByValue)) {
                select.setIsSelected("selected");
                filterData.setSearchColumn(select.getColumnName());
            } else {
                select.setIsSelected("");
            }
        }
        filterData.setSearchText(searchText);
        session.setAttribute(attribute.getName(), filterData);
        return filterData;
    }
}
