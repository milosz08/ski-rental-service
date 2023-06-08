/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: RentDetailsResDto.java
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
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import pl.polsl.skirentalservice.util.Gender;
import pl.polsl.skirentalservice.util.RentStatus;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RentDetailsResDto {
    private Long id;
    private String issuedIdentifier;
    private LocalDateTime issuedDateTime;
    private LocalDateTime rentDateTime;
    private LocalDateTime returnDateTime;
    private String description;
    private Integer tax;
    private RentStatus status;
    private BigDecimal totalPriceNetto;
    private BigDecimal totalPriceBrutto;
    private BigDecimal totalDepositPriceNetto;
    private BigDecimal totalDepositPriceBrutto;
    private String fullName;
    private String pesel;
    private LocalDate bornDate;
    private String phoneNumber;
    private Integer age;
    private String emailAddress;
    private Gender gender;
    private String cityWithPostalCode;
    private String address;
}
