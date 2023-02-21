/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 * Silesian University of Technology
 *
 *  File name: SellerRentReturnRecordResDto.java
 *  Last modified: 31/01/2023, 00:41
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

public record SellerRentReturnRecordResDto(
    Long id,
    String issuedIdentifier,
    LocalDateTime issuedDateTime,
    BigDecimal totalPriceNetto,
    BigDecimal totalPriceBrutto,
    Long rentId,
    String rentIssuedIdentifier
) {
}
