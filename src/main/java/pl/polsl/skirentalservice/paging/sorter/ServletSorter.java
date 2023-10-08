/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.paging.sorter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import pl.polsl.skirentalservice.util.SessionAttribute;

import java.util.Map;
import java.util.Objects;

public class ServletSorter {

    private final HttpServletRequest req;
    private final String defColumnSort;
    private final Map<String, ServletSorterField> fieldsMap;

    public ServletSorter(HttpServletRequest req, String defColumnSort, Map<String, ServletSorterField> fieldsMap) {
        this.req = req;
        this.defColumnSort = defColumnSort;
        this.fieldsMap = fieldsMap;
    }

    public SorterDataDto generateSortingJPQuery(SessionAttribute attribute) {
        final HttpSession session = req.getSession();
        SorterDataDto sortedData = (SorterDataDto) session.getAttribute(attribute.getName());
        if (Objects.isNull(sortedData)) sortedData = new SorterDataDto(fieldsMap, defColumnSort);

        final String columnName = req.getParameter("sortBy");
        if (Objects.isNull(columnName)) return sortedData;

        for (final Map.Entry<String, ServletSorterField> entry : sortedData.getFieldsMap().entrySet()) {
            final ServletSorterField field = entry.getValue();
            if (entry.getKey().equals(columnName)) {
                switch (field.getDirection()) {
                    case IDLE -> sortedData.setJpql(field.setAscending());
                    case ASC -> sortedData.setJpql(field.setDescending());
                    default -> sortedData.setJpql(field.reset(defColumnSort));
                }
            } else {
                field.reset(defColumnSort);
            }
        }
        session.setAttribute(attribute.getName(), sortedData);
        return sortedData;
    }
}
