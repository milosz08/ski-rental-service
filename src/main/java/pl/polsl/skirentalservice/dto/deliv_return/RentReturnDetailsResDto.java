/*
 * Copyright (c) 2023 by multiple authors
 * Silesian University of Technology
 *
 *  File name: RentReturnDetailsResDto.java
 *  Last modified: 30/01/2023, 23:10
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.dto.deliv_return;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Data
@AllArgsConstructor
public class RentReturnDetailsResDto {
    private String issuedIdentifier;
    private LocalDateTime rentDateTime;
    private Integer tax;
    private BigDecimal totalDepositPrice;
}
