/*
 * Copyright (c) 2023 by multiple authors
 * Silesian University of Technology
 *
 *  File name: ServletSorter.java
 *  Last modified: 21/01/2023, 16:18
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.sorter;

import java.util.Map;
import static pl.polsl.skirentalservice.sorter.SortDirection.*;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public class ServletSorter {

    private final Map<String, ServletSorterField> sorterFieldMap;
    private final String columnName;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public ServletSorter(Map<String, ServletSorterField> sorterFieldMap, String columnName) {
        this.sorterFieldMap = sorterFieldMap;
        this.columnName = columnName;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public String setActiveFieldsAndSwapDirection() {
        String jpql = "";
        for (final Map.Entry<String, ServletSorterField> entry : sorterFieldMap.entrySet()) {
            final ServletSorterField value = entry.getValue();
            if (entry.getKey().equals(columnName)) {
                jpql = value.getJpql() + value.getDirection().getDir();
                value.setActive(true);
                value.setChevronBts(value.getDirection().equals(ASC) ? "down" : "up");
                value.setDirection(value.getDirection().equals(ASC) ? DESC : ASC);
            } else {
                value.setChevronBts("down");
                value.setDirection(ASC);
                value.setActive(false);
            }
        }
        return jpql;
    }
}
