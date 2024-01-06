/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.pdf.dto;

import lombok.Data;

@Data
public class PdfEquipmentDataDto {
    private String name;
    private String count;
    private String priceNetto;
    private String priceBrutto;
    private String depositPriceNetto;
    private String depositPriceBrutto;

    @Override
    public String toString() {
        return "{" +
            "name=" + name +
            ", count=" + count +
            ", priceNetto=" + priceNetto +
            ", priceBrutto=" + priceBrutto +
            ", depositPriceNetto=" + depositPriceNetto +
            ", depositPriceBrutto=" + depositPriceBrutto +
            '}';
    }
}
