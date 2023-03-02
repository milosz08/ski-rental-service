/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 * Silesian University of Technology
 *
 *  File name: PriceUnitsDto.java
 *  Last modified: 29/01/2023, 12:16
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
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
