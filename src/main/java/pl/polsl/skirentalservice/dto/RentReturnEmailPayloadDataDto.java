/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: RentReturnEmailPayloadDataDto.java
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
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.HashSet;
import java.math.BigDecimal;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Data
@NoArgsConstructor
public class RentReturnEmailPayloadDataDto {
    private String fullName;
    private String pesel;
    private String phoneNumber;
    private String email;
    private String rentDate;
    private String returnDate;
    private String rentTime;
    private String tax;
    private BigDecimal totalPriceBrutto;
    private BigDecimal totalPriceNetto;
    private BigDecimal totalDepositPriceBrutto;
    private BigDecimal totalPriceWithDepositBrutto;
    private Set<EmailEquipmentPayloadDataDto> rentEquipments = new HashSet<>();

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String toString() {
        return "{" +
            "fullName='" + fullName +
            ", pesel='" + pesel +
            ", phoneNumber='" + phoneNumber +
            ", email='" + email +
            ", rentDate='" + rentDate +
            ", returnDate='" + returnDate +
            ", rentTime='" + rentTime +
            ", tax='" + tax +
            ", totalPriceBrutto=" + totalPriceBrutto +
            ", totalPriceNetto=" + totalPriceNetto +
            ", totalDepositPriceBrutto=" + totalDepositPriceBrutto +
            ", totalPriceWithDepositBrutto=" + totalPriceWithDepositBrutto +
            ", rentEquipments=" + rentEquipments +
            '}';
    }
}
