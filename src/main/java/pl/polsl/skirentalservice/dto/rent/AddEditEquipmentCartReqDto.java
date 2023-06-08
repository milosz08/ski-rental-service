/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: AddEditEquipmentCartReqDto.java
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

import pl.polsl.skirentalservice.util.Regex;
import pl.polsl.skirentalservice.core.IReqValidatePojo;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Data
public class AddEditEquipmentCartReqDto implements IReqValidatePojo {

    @NotEmpty(message = "Pole ilości sprzętów w wypożyczeniu nie może być puste.")
    @Pattern(regexp = Regex.POS_NUMBER_INT, message = "Pole ilości sprzętów w wypożyczeniu może przyjmować wartości od 1 do 9999.")
    private String count;

    @Pattern(regexp = Regex.DECIMAL_2_ROUND, message = "Pole kaucji musi być w formacie 0,00 lub 0.00, wartość min 1,00.")
    private String depositPrice;

    @Size(max = 200, message = "Pole dodatkowych uwag może mieć maksymalnie 200 znaków.")
    private String description;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public AddEditEquipmentCartReqDto(HttpServletRequest req) {
        this.count = StringUtils.trimToEmpty(req.getParameter("count"));
        this.depositPrice = StringUtils.trimToEmpty(req.getParameter("depositPrice")).replace(',', '.');
        this.description = StringUtils.trimToNull(req.getParameter("description"));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String toString() {
        return "{" +
            "count='" + count +
            ", depositPrice='" + depositPrice +
            ", description='" + description +
            '}';
    }
}
