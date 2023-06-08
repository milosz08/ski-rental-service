/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: PriceUnitsDto.java
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

import java.math.BigDecimal;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Data
public class PriceUnitsDto {
    private BigDecimal totalPriceNetto;
    private BigDecimal totalPriceBrutto;
    private BigDecimal totalDepositPriceNetto;
    private BigDecimal totalDepositPriceBrutto;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public PriceUnitsDto() {
        this.totalPriceNetto = new BigDecimal("0.00");
        this.totalPriceBrutto = new BigDecimal("0.00");
        this.totalDepositPriceNetto =new BigDecimal("0.00");
        this.totalDepositPriceBrutto = new BigDecimal("0.00");
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void add(BigDecimal netto, BigDecimal brutto, BigDecimal depositNetto, BigDecimal depositBrutto) {
        totalPriceNetto = totalPriceNetto.add(netto);
        totalPriceBrutto = totalPriceBrutto.add(brutto);
        totalDepositPriceNetto = totalDepositPriceNetto.add(depositNetto);
        totalDepositPriceBrutto = totalDepositPriceBrutto.add(depositBrutto);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String toString() {
        return "{" +
            "totalPriceNetto=" + totalPriceNetto +
            ", totalPriceBrutto=" + totalPriceBrutto +
            ", totalDepositPriceNetto=" + totalDepositPriceNetto +
            ", totalDepositPriceBrutto=" + totalDepositPriceBrutto +
            '}';
    }
}
