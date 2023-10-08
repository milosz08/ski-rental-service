/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.dto.deliv_return;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
