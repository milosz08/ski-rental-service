/*
 * Copyright (c) 2023 by multiple authors
 * Silesian University of Technology
 *
 *  File name: ServletSorter.java
 *  Last modified: 22/01/2023, 16:05
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.paging.sorter;

import java.util.Map;
import jakarta.servlet.http.*;

import pl.polsl.skirentalservice.util.SessionAttribute;

import static java.util.Objects.*;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public class ServletSorter {

    private final HttpServletRequest req;
    private final String defColumnSort;
    private final Map<String, ServletSorterField> fieldsMap;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public ServletSorter(HttpServletRequest req, String defColumnSort, Map<String, ServletSorterField> fieldsMap) {
        this.req = req;
        this.defColumnSort = defColumnSort;
        this.fieldsMap = fieldsMap;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public SorterDataDto generateSortingJPQuery(SessionAttribute attribute) {
        final HttpSession session = req.getSession();
        SorterDataDto sortedData = (SorterDataDto) session.getAttribute(attribute.getName());
        if (isNull(sortedData)) sortedData = new SorterDataDto(fieldsMap, defColumnSort);

        final String columnName = req.getParameter("sortBy");
        if (isNull(columnName)) return sortedData;

        final ServletSorterField sorterField = sortedData.getFieldsMap().get(columnName);

        for (final Map.Entry<String, ServletSorterField> entry : sortedData.getFieldsMap().entrySet()) {
            final ServletSorterField field = entry.getValue();
            if (entry.getKey().equals(columnName)) {
                switch (field.getDirection()) {
                    case IDLE:  sortedData.setJpql(field.setAscending()); break;
                    case ASC:   sortedData.setJpql(field.setDescending()); break;
                    default:    sortedData.setJpql(field.reset(defColumnSort));
                }
            } else {
                field.reset(defColumnSort);
            }
        }
        session.setAttribute(attribute.getName(), sortedData);
        return sortedData;
    }
}
