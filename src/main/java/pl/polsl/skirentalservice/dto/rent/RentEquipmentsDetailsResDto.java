/*
 * Copyright (c) 2023 by multiple authors
 * Silesian University of Technology
 *
 *  File name: SelerRentEquipmentsDetailsResDto.java
 *  Last modified: 29/01/2023, 23:40
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.dto.rent;

import lombok.*;
import java.math.BigDecimal;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Data
@AllArgsConstructor
public class RentEquipmentsDetailsResDto {
    private Long id;
    private String name;
    private Integer count;
    private String barcode;
    private String description;
    private BigDecimal priceNetto;
    private BigDecimal priceBrutto;
    private BigDecimal depositPriceNetto;
    private BigDecimal depositPriceBrutto;
}
