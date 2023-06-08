/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: NewRentDetailsReqDto.java
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

package pl.polsl.skirentalservice.dto.rent;

import lombok.Data;

import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.NotEmpty;
import jakarta.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import pl.polsl.skirentalservice.util.Regex;
import pl.polsl.skirentalservice.core.IReqValidatePojo;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Data
public class NewRentDetailsReqDto implements IReqValidatePojo {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @NotEmpty(message = "Pole daty wypożyczenia nie może być puste.")
    @Pattern(regexp = Regex.DATE_TIME, message = "Nieprawidłowa wartość w polu data wypożyczenia.")
    private String rentDateTime;

    @NotEmpty(message = "Pole daty zwrotu wypożyczenia nie może być puste.")
    @Pattern(regexp = Regex.DATE_TIME, message = "Nieprawidłowa wartość w polu data zwrotu wypożyczenia.")
    private String returnDateTime;

    @NotEmpty(message = "Pole wartości procentowej podatku nie może być puste.")
    @Pattern(regexp = Regex.TAX, message = "Nieprawidłowa wartość w polu wartości procentowej podatku.")
    private String tax;

    @Size(max = 200, message = "Pole dodatkowych uwag do składanego wypożyczenia może mieć maksymalnie 200 znaków.")
    private String description;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public NewRentDetailsReqDto(HttpServletRequest req) {
        this.rentDateTime = StringUtils.trimToEmpty(req.getParameter("rentDateTime"));
        this.returnDateTime = StringUtils.trimToEmpty(req.getParameter("returnDateTime"));
        this.tax = StringUtils.trimToEmpty(req.getParameter("tax")).replace(',', '.');
        this.description = StringUtils.trimToNull(req.getParameter("description"));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public LocalDateTime getParsedRentDateTime() {
        return LocalDateTime.parse(rentDateTime.replace('T', ' '), formatter);
    }

    public LocalDateTime getParsedReturnDateTime() {
        return LocalDateTime.parse(returnDateTime.replace('T', ' '), formatter);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String toString() {
        return "{" +
            "rentDateTime='" + rentDateTime +
            ", returnDateTime='" + returnDateTime +
            ", tax='" + tax +
            ", description='" + description +
            '}';
    }
}
