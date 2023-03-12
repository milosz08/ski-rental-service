/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 * Silesian University of Technology
 *
 *  File name: OwnerRentReturnRecordResDto.java
 *  Last modified: 31/01/2023, 00:42
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.dto.deliv_return;

import lombok.Data;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Data
@AllArgsConstructor
public class OwnerRentReturnRecordResDto {
    private Long id;
    private String issuedIdentifier;
    private LocalDateTime issuedDateTime;
    private BigDecimal totalPriceNetto;
    private BigDecimal totalPriceBrutto;
    private Long rentId;
    private String rentIssuedIdentifier;
    private Long employerId;
    private String employerFullName;
}
