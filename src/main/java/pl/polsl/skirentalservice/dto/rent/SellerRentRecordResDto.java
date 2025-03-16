package pl.polsl.skirentalservice.dto.rent;

import pl.polsl.skirentalservice.util.RentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SellerRentRecordResDto(
    Long id,
    String issuedIdentifier,
    LocalDateTime issuedDateTime,
    RentStatus status,
    Boolean isRented,
    BigDecimal totalPriceNetto,
    BigDecimal totalPriceBrutto,
    String customerName,
    Long customerId
) {
}
