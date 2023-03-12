/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 * Silesian University of Technology
 *
 *  File name: RentDetailsResDto.java
 *  Last modified: 31/01/2023, 07:42
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.dto.rent;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import pl.polsl.skirentalservice.util.Gender;
import pl.polsl.skirentalservice.util.RentStatus;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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
