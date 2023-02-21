/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 * Silesian University of Technology
 *
 *  File name: RentReturnDetailsResDto.java
 *  Last modified: 07/02/2023, 15:45
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.dto.deliv_return;

import java.math.BigDecimal;
import java.time.LocalDateTime;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public record RentReturnDetailsResDto(
    String issuedIdentifier,
    LocalDateTime rentDateTime,
    Integer tax,
    BigDecimal totalPriceNetto,
    BigDecimal totalPriceBrutto,
    BigDecimal totalDepositPriceNetto,
    BigDecimal totalDepositPriceBrutto
) {
}
