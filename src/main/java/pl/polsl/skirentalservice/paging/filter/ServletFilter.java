/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: ServletFilter.java
 * Last modified: 3/12/23, 11:01 AM
 * Project name: ski-rental-service
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 *
 *     <http://www.apache.org/license/LICENSE-2.0>
 *
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the license.
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
