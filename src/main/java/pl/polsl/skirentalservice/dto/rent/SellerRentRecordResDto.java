/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.dto.rent;

import pl.polsl.skirentalservice.util.RentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SellerRentRecordResDto(
    Long id,
    String issuedIdentifier,
    LocalDateTime issuedDateTime,
    RentStatus status,
    BigDecimal totalPriceNetto,
    BigDecimal totalPriceBrutto,
    String customerName,
    Long customerId
) {
}
