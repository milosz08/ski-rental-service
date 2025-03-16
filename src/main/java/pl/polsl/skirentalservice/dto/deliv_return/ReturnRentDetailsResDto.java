package pl.polsl.skirentalservice.dto.deliv_return;

import pl.polsl.skirentalservice.util.Gender;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ReturnRentDetailsResDto(
    Long id,
    String issuedIdentifier,
    String rentIssuedIdentifier,
    LocalDateTime issuedDateTime,
    String description,
    Integer tax,
    BigDecimal totalPriceNetto,
    BigDecimal totalPriceBrutto,
    BigDecimal totalDepositPriceNetto,
    BigDecimal totalDepositPriceBrutto,
    String fullName,
    String pesel,
    LocalDate bornDate,
    String phoneNumber,
    Integer age,
    String emailAddress,
    Gender gender,
    String cityWithPostalCode,
    String address
) {
}
