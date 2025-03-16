package pl.polsl.skirentalservice.dto.rent;

import pl.polsl.skirentalservice.util.RentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OwnerRentRecordResDto(
    Long id,
    String issuedIdentifier,
    LocalDateTime issuedDateTime,
    RentStatus status,
    BigDecimal totalPriceNetto,
    BigDecimal totalPriceBrutto,
    String customerName,
    Long customerId,
    String employerName,
    Long employerId
) {
}
