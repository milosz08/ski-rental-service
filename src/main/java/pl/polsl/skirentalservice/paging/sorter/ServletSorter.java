/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: ServletSorter.java
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

package pl.polsl.skirentalservice.paging.sorter;

import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;
import java.util.Objects;

import pl.polsl.skirentalservice.util.SessionAttribute;

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
        if (Objects.isNull(sortedData)) sortedData = new SorterDataDto(fieldsMap, defColumnSort);

        final String columnName = req.getParameter("sortBy");
        if (Objects.isNull(columnName)) return sortedData;

        final ServletSorterField sorterField = sortedData.getFieldsMap().get(columnName);

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
