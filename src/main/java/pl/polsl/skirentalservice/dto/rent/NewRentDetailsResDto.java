/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: NewRentDetailsResDto.java
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

package pl.polsl.skirentalservice.dto.rent;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import pl.polsl.skirentalservice.util.RentStatus;
import pl.polsl.skirentalservice.core.ValidatorSingleton;
import pl.polsl.skirentalservice.dto.FormValueInfoTupleDto;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Data
@NoArgsConstructor
public class NewRentDetailsResDto {
    private String issuedIdentifier;
    private String issuedDateTime;
    private RentStatus rentStatus;
    private FormValueInfoTupleDto rentDateTime = new FormValueInfoTupleDto();
    private FormValueInfoTupleDto returnDateTime = new FormValueInfoTupleDto();
    private FormValueInfoTupleDto tax = new FormValueInfoTupleDto();
    private FormValueInfoTupleDto description  = new FormValueInfoTupleDto();

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public NewRentDetailsResDto(ValidatorSingleton validator, NewRentDetailsReqDto reqDto) {
        this.rentDateTime = validator.validateField(reqDto, "rentDateTime", reqDto.getRentDateTime());
        this.returnDateTime = validator.validateField(reqDto, "returnDateTime", reqDto.getReturnDateTime());
        this.tax = validator.validateField(reqDto, "tax", reqDto.getTax());
        this.description = validator.validateField(reqDto, "description", reqDto.getDescription());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public LocalDateTime getParsedIssuedDateTime() {
        return LocalDateTime.parse(issuedDateTime.replace('T', ' '), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String toString() {
        return "{" +
            "issuedIdentifier='" + issuedIdentifier +
            ", issuedDateTime='" + issuedDateTime +
            ", rentStatus=" + rentStatus +
            ", rentDateTime=" + rentDateTime +
            ", returnDateTime=" + returnDateTime +
            ", tax=" + tax +
            ", description=" + description +
            '}';
    }
}
