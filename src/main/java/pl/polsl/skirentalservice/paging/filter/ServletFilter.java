/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.paging.filter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import pl.polsl.skirentalservice.util.SessionAttribute;

import java.util.List;

@RequiredArgsConstructor
public class ServletFilter {
    private final HttpServletRequest req;
    private final List<FilterColumn> fields;

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
                select.setIsSelected(StringUtils.EMPTY);
            }
        }
        filterData.setSearchText(searchText);
        session.setAttribute(attribute.getName(), filterData);
        return filterData;
    }
}
