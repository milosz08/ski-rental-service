/*
 * Copyright (c) 2023 by multiple authors
 * Silesian University of Technology
 *
 *  File name: PdfEquipmentDataDto.java
 *  Last modified: 08/02/2023, 21:22
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.pdf.dto;

import lombok.Data;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Data
public class PdfEquipmentDataDto {
    private String name;
    private String count;
    private String priceNetto;
    private String priceBrutto;
    private String depositPriceNetto;
    private String depositPriceBrutto;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String toString() {
        return "{" +
            "name='" + name +
            ", count='" + count +
            ", priceNetto='" + priceNetto +
            ", priceBrutto='" + priceBrutto +
            ", depositPriceNetto='" + depositPriceNetto +
            ", depositPriceBrutto='" + depositPriceBrutto +
            '}';
    }
}
