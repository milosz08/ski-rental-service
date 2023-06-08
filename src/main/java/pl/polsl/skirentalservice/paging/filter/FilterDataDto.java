/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: FilterDataDto.java
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

import lombok.Data;
import lombok.AllArgsConstructor;

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
