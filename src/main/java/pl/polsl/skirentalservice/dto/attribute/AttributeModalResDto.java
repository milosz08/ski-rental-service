/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: AttributeModalResDto.java
 * Last modified: 6/2/23, 11:49 PM
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

package pl.polsl.skirentalservice.dto.attribute;

import lombok.Data;
import lombok.NoArgsConstructor;

import pl.polsl.skirentalservice.core.ValidatorSingleton;
import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.dto.FormValueInfoTupleDto;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Data
@NoArgsConstructor
public class AttributeModalResDto {
    private FormValueInfoTupleDto name;
    private AlertTupleDto alert;
    private String immediatelyShow;
    private NavCanvasState activeFirstPage = new NavCanvasState(true);
    private NavCanvasState activeSecondPage = new NavCanvasState(false);

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public AttributeModalResDto(ValidatorSingleton validator, AttributeModalReqDto reqDto, AlertTupleDto alert) {
        this.name = validator.validateField(reqDto, "name", reqDto.getName());
        this.alert = alert;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void setModalImmediatelyOpen(boolean isImmediatelyOpen) {
        this.immediatelyShow = isImmediatelyOpen ? "open" : "close";
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String toString() {
        return "{" +
            "name=" + name +
            ", alert=" + alert +
            '}';
    }
}
