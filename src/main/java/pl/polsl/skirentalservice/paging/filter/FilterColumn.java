/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: FilterColumn.java
 * Last modified: 2/10/23, 7:56 PM
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

import lombok.Getter;

import pl.polsl.skirentalservice.dto.FormSelectTupleDto;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Getter
public class FilterColumn extends FormSelectTupleDto {
    private final String columnName;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public FilterColumn(String value, String text, String columnName) {
        this(false, value, text, columnName);
    }

    public FilterColumn(boolean isSelected, String value, String text, String columnName) {
        super(isSelected, value, text);
        this.columnName = columnName;
    }
}
