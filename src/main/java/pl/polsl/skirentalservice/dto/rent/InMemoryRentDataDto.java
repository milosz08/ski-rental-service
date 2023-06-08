/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: InMemoryRentDataDto.java
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

import java.util.List;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.format.DateTimeFormatter;

import pl.polsl.skirentalservice.util.RentStatus;
import pl.polsl.skirentalservice.dto.PriceUnitsDto;
import pl.polsl.skirentalservice.dto.customer.CustomerDetailsResDto;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Data
public class InMemoryRentDataDto {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private Long customerId;
    private String customerFullName;
    private String issuedIdentifier;
    private String issuedDateTime;
    private String rentDateTime;
    private String returnDateTime;
    private RentStatus rentStatus;
    private String description;
    private String tax;
    private PriceUnitsDto priceUnits;
    private long days;
    private long hours;
    private int totalCount;
    private boolean allGood;
    private CustomerDetailsResDto customerDetails;
    private List<CartSingleEquipmentDataDto> equipments = new ArrayList<>();

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public InMemoryRentDataDto(Long customerId, String customerFullName) {
        this.customerId = customerId;
        this.customerFullName = customerFullName;
        this.issuedDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES).toString().replace('T', ' ');
        this.rentStatus = RentStatus.OPENED;
        this.priceUnits = new PriceUnitsDto();
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
            "customerId=" + customerId +
            ", customerFullName='" + customerFullName +
            ", issuedIdentifier='" + issuedIdentifier +
            ", issuedDateTime='" + issuedDateTime +
            ", rentDateTime='" + rentDateTime +
            ", returnDateTime='" + returnDateTime +
            ", rentStatus=" + rentStatus +
            ", description='" + description +
            ", tax='" + tax +
            ", priceUnits=" + priceUnits +
            ", days=" + days +
            ", hours=" + hours +
            ", totalCount=" + totalCount +
            ", allGood=" + allGood +
            ", equipments=" + equipments +
            '}';
    }
}
