/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.dto.deliv_return;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
