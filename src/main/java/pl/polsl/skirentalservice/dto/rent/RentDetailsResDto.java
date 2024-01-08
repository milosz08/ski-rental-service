/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.dto.rent;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.polsl.skirentalservice.util.Gender;
import pl.polsl.skirentalservice.util.RentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RentDetailsResDto {
    private Long id;
    private String issuedIdentifier;
    private LocalDateTime issuedDateTime;
    private LocalDateTime rentDateTime;
    private LocalDateTime returnDateTime;
    private String description;
    private Integer tax;
    private RentStatus status;
    private Boolean isRented;
    private BigDecimal totalPriceNetto;
    private BigDecimal totalPriceBrutto;
    private BigDecimal totalDepositPriceNetto;
    private BigDecimal totalDepositPriceBrutto;
    private String fullName;
    private String pesel;
    private LocalDate bornDate;
    private String phoneNumber;
    private Integer age;
    private String emailAddress;
    private Gender gender;
    private String cityWithPostalCode;
    private String address;
}
