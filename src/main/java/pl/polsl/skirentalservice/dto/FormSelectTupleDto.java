/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: FormSelectTupleDto.java
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

package pl.polsl.skirentalservice.dto;

import lombok.Data;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Data
public class FormSelectTupleDto {
    private String value;
    private String text;
    private String isSelected;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public FormSelectTupleDto(boolean isSelected, String value, String text) {
        this.value = value;
        this.text = text;
        this.isSelected = isSelected ? "selected" : "";
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public FormSelectTupleDto(String value, String text) {
        this.value = value;
        this.text = text;
    }
}
