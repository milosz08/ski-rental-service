/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 * Silesian University of Technology
 *
 *  File name: ReturnRentDetailsResDto.java
 *  Last modified: 31/01/2023, 07:41
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.dto.deliv_return;

import java.time.*;
import java.math.BigDecimal;

import pl.polsl.skirentalservice.util.Gender;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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
