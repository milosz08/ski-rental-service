/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 * Silesian University of Technology
 *
 *  File name: SellerRentRecordResDto.java
 *  Last modified: 29/01/2023, 21:42
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.dto.rent;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import pl.polsl.skirentalservice.util.RentStatus;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Data
@AllArgsConstructor
public class SellerRentRecordResDto {
    private Long id;
    private String issuedIdentifier;
    private LocalDateTime issuedDateTime;
    private RentStatus status;
    private BigDecimal totalPriceNetto;
    private BigDecimal totalPriceBrutto;
    private String customerName;
    private Long customerId;
}
