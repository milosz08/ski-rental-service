/*
 * Copyright (c) 2023 by multiple authors
 * Silesian University of Technology
 *
 *  File name: OwnerRentRecordResDto.java
 *  Last modified: 30/01/2023, 00:31
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.dto.rent;

import lombok.*;
import pl.polsl.skirentalservice.util.RentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Data
@AllArgsConstructor
public class OwnerRentRecordResDto {
    private Long id;
    private String issuedIdentifier;
    private LocalDateTime issuedDateTime;
    private RentStatus status;
    private BigDecimal totalPriceNetto;
    private BigDecimal totalPriceBrutto;
    private String customerName;
    private Long customerId;
    private String employerName;
    private Long employerId;
}
