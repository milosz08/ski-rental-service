/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: EmailEquipmentPayloadDataDto.java
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

import java.math.BigDecimal;

import pl.polsl.skirentalservice.entity.EquipmentEntity;
import pl.polsl.skirentalservice.dto.deliv_return.RentReturnEquipmentRecordResDto;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Data
@NoArgsConstructor
public class EmailEquipmentPayloadDataDto {
    private String count;
    private String name;
    private String typeAndModel;
    private BigDecimal priceNetto;
    private BigDecimal priceBrutto;
    private BigDecimal depositPriceNetto;
    private BigDecimal depositPriceBrutto;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public EmailEquipmentPayloadDataDto(EquipmentEntity eqEntity, RentReturnEquipmentRecordResDto eqDto) {
        name = eqEntity.getName();
        count = eqDto.count().toString();
        typeAndModel = eqEntity.getModel() + ", " + eqEntity.getEquipmentType().getName();
        depositPriceNetto = eqDto.depositPriceNetto();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String toString() {
        return "{" +
            "count='" + count +
            ", name='" + name +
            ", typeAndModel='" + typeAndModel +
            ", priceNetto=" + priceNetto +
            ", priceBrutto=" + priceBrutto +
            ", depositPriceNetto=" + depositPriceNetto +
            ", depositPriceBrutto=" + depositPriceBrutto +
            '}';
    }
}
