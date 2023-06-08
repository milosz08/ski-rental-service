/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: ReturnPdfDocumentDataDto.java
 * Last modified: 3/23/23, 3:21 AM
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

package pl.polsl.skirentalservice.pdf.dto;

import lombok.Data;

import java.util.List;
import java.util.ArrayList;

import pl.polsl.skirentalservice.dto.PriceUnitsDto;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Data
public class ReturnPdfDocumentDataDto {
    private String issuedIdentifier;
    private String fullName;
    private String pesel;
    private String phoneNumber;
    private String email;
    private String address;
    private String rentDate;
    private String returnDate;
    private String rentTime;
    private String tax;
    private String totalSumPriceNetto;
    private String totalSumPriceBrutto;
    private String description;
    private PriceUnitsDto priceUnits = new PriceUnitsDto();
    private List<PdfEquipmentDataDto> equipments = new ArrayList<>();

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String toString() {
        return "{" +
            "issuedIdentifier='" + issuedIdentifier +
            ", fullName='" + fullName +
            ", pesel='" + pesel +
            ", phoneNumber='" + phoneNumber +
            ", email='" + email +
            ", address='" + address +
            ", rentDate='" + rentDate +
            ", returnDate='" + returnDate +
            ", rentTime='" + rentTime +
            ", tax='" + tax +
            ", totalSumPriceNetto='" + totalSumPriceNetto +
            ", totalSumPriceBrutto='" + totalSumPriceBrutto +
            ", description='" + description +
            ", priceUnits=" + priceUnits +
            ", equipments=" + equipments +
            '}';
    }
}
