/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: AttributeModalReqDto.java
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

package pl.polsl.skirentalservice.dto.attribute;

import lombok.Data;

import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotEmpty;
import jakarta.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import pl.polsl.skirentalservice.core.IReqValidatePojo;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Data
public class AttributeModalReqDto implements IReqValidatePojo {

    @NotEmpty(message = "Pole nie może być puste.")
    @Size(min = 5, max = 50, message = "Pole musi zawierać od 5 do 50 znaków.")
    private String name;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public AttributeModalReqDto(HttpServletRequest req) {
        this.name = StringUtils.trimToEmpty(req.getParameter("name"));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String toString() {
        return "{" +
            "name='" + name +
            '}';
    }
}
