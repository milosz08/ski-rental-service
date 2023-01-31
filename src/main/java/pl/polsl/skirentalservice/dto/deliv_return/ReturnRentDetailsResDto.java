/*
 * Copyright (c) 2023 by multiple authors
 * Silesian University of Technology
 *
 *  File name: ReturnRentDetailsResDto.java
 *  Last modified: 31/01/2023, 07:13
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.dto.deliv_return;

import lombok.*;

import java.time.*;
import java.math.BigDecimal;

import pl.polsl.skirentalservice.util.Gender;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Data
@AllArgsConstructor
public class ReturnRentDetailsResDto {
    private Long id;
    private String issuedIdentifier;
    private String rentIssuedIdentifier;
    private LocalDateTime issuedDateTime;
    private String description;
    private Integer tax;
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
