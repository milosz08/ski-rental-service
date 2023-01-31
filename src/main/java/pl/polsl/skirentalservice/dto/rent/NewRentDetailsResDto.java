/*
 * Copyright (c) 2023 by multiple authors
 * Silesian University of Technology
 *
 *  File name: NewRentDetailsResDto.java
 *  Last modified: 28/01/2023, 16:56
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.dto.rent;

import lombok.*;
import java.time.LocalDateTime;

import pl.polsl.skirentalservice.util.RentStatus;
import pl.polsl.skirentalservice.core.ValidatorBean;
import pl.polsl.skirentalservice.dto.FormValueInfoTupleDto;

import static java.time.LocalDateTime.parse;
import static java.time.format.DateTimeFormatter.ofPattern;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Data
@NoArgsConstructor
public class NewRentDetailsResDto {
    private String issuedIdentifier;
    private String issuedDateTime;
    private RentStatus rentStatus;
    private FormValueInfoTupleDto rentDateTime = new FormValueInfoTupleDto();
    private FormValueInfoTupleDto returnDateTime = new FormValueInfoTupleDto();
    private FormValueInfoTupleDto tax = new FormValueInfoTupleDto();
    private FormValueInfoTupleDto description  = new FormValueInfoTupleDto();

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public NewRentDetailsResDto(ValidatorBean validator, NewRentDetailsReqDto reqDto) {
        this.rentDateTime = validator.validateField(reqDto, "rentDateTime", reqDto.getRentDateTime());
        this.returnDateTime = validator.validateField(reqDto, "returnDateTime", reqDto.getReturnDateTime());
        this.tax = validator.validateField(reqDto, "tax", reqDto.getTax());
        this.description = validator.validateField(reqDto, "description", reqDto.getDescription());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public LocalDateTime getParsedIssuedDateTime() {
        return parse(issuedDateTime.replace('T', ' '), ofPattern("yyyy-MM-dd HH:mm"));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String toString() {
        return "{" +
            "issuedIdentifier='" + issuedIdentifier +
            ", issuedDateTime='" + issuedDateTime +
            ", rentStatus=" + rentStatus +
            ", rentDateTime=" + rentDateTime +
            ", returnDateTime=" + returnDateTime +
            ", tax=" + tax +
            ", description=" + description +
            '}';
    }
}