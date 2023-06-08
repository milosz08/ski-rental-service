/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: CartSingleEquipmentDataDto.java
 * Last modified: 2/10/23, 7:52 PM
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
import java.math.BigDecimal;

import pl.polsl.skirentalservice.dto.PriceUnitsDto;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Data
public class CartSingleEquipmentDataDto {
    private Long id;
    private String name;
    private String typeAndModel;
    private String count;
    private String totalPersistCount;
    private String description;
    private PriceUnitsDto priceUnits;
    private AddEditEquipmentCartResDto resDto;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public CartSingleEquipmentDataDto(
        EquipmentRentRecordResDto equipmentDetails, AddEditEquipmentCartReqDto reqDto, AddEditEquipmentCartResDto resDto
    ) {
        id =  equipmentDetails.getId();
        typeAndModel = equipmentDetails.getType() + ", " + equipmentDetails.getModel();
        description = reqDto.getDescription();
        count = reqDto.getCount();
        priceUnits = new PriceUnitsDto();
        if (!reqDto.getDepositPrice().isEmpty()) {
            priceUnits.setTotalDepositPriceNetto(new BigDecimal(reqDto.getDepositPrice()));
        }
        this.resDto = resDto;
        totalPersistCount = equipmentDetails.getTotalCount().toString();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void setPrices(BigDecimal sumNetto, BigDecimal sumBrutto, BigDecimal depositBrutto) {
        priceUnits.setTotalPriceNetto(sumNetto);
        priceUnits.setTotalPriceBrutto(sumBrutto);
        priceUnits.setTotalDepositPriceBrutto(depositBrutto);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String toString() {
        return "{" +
            "id=" + id +
            ", name='" + name +
            ", typeAndModel='" + typeAndModel +
            ", count='" + count +
            ", totalPersistCount='" + totalPersistCount +
            ", description='" + description +
            ", priceUnits=" + priceUnits +
            ", resDto=" + resDto +
            '}';
    }
}
