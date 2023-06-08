/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: EquipmentRentRecordResDto.java
 * Last modified: 6/3/23, 5:02 AM
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

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EquipmentRentRecordResDto {
    private Long id;
    private String name;
    private String type;
    private String model;
    private String barcode;
    private Integer totalCount;
    private BigDecimal pricePerHour;
    private BigDecimal priceForNextHour;
    private BigDecimal pricePerDay;
    private String disabled;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void setDisabled(boolean isDisabled) {
        this.disabled = isDisabled ? "disabled" : "";
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String toString() {
        return "{" +
            "id=" + id +
            ", name='" + name +
            ", type='" + type +
            ", model='" + model +
            ", barcode='" + barcode +
            ", totalCount=" + totalCount +
            ", pricePerHour=" + pricePerHour +
            ", priceForNextHour=" + priceForNextHour +
            ", pricePerDay=" + pricePerDay +
            ", disabled='" + disabled +
            '}';
    }
}
