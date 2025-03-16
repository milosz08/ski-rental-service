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
