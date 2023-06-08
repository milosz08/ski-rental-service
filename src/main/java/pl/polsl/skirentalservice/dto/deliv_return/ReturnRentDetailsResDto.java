/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: ReturnRentDetailsResDto.java
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

package pl.polsl.skirentalservice.dto.deliv_return;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.math.BigDecimal;

import pl.polsl.skirentalservice.util.Gender;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public record ReturnRentDetailsResDto(
    Long id,
    String issuedIdentifier,
    String rentIssuedIdentifier,
    LocalDateTime issuedDateTime,
    String description,
    Integer tax,
    BigDecimal totalPriceNetto,
    BigDecimal totalPriceBrutto,
    BigDecimal totalDepositPriceNetto,
    BigDecimal totalDepositPriceBrutto,
    String fullName,
    String pesel,
    LocalDate bornDate,
    String phoneNumber,
    Integer age,
    String emailAddress,
    Gender gender,
    String cityWithPostalCode,
    String address
) {
}
