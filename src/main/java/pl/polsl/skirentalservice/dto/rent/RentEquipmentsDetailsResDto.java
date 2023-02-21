/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 * Silesian University of Technology
 *
 *  File name: RentEquipmentsDetailsResDto.java
 *  Last modified: 30/01/2023, 01:41
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.dto.rent;

import java.math.BigDecimal;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public record RentEquipmentsDetailsResDto(
    Long id,
    String name,
    Integer count,
    String barcode,
    String description,
    BigDecimal priceNetto,
    BigDecimal priceBrutto,
    BigDecimal depositPriceNetto,
    BigDecimal depositPriceBrutto
) {
}
