/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.dto.deliv_return;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
