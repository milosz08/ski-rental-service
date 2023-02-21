/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 * Silesian University of Technology
 *
 *  File name: AttributeValidatorPayloadDto.java
 *  Last modified: 27/01/2023, 02:54
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.dto.attribute;

import lombok.*;
import pl.polsl.skirentalservice.dto.AlertTupleDto;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Builder
public record AttributeValidatorPayloadDto (
    AlertTupleDto alert,
    AttributeModalReqDto reqDto,
    AttributeModalResDto resDto,
    boolean isInvalid
) {
}
