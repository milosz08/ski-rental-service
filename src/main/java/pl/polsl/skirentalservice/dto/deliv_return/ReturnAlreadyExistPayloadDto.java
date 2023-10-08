/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.dto.deliv_return;

public record ReturnAlreadyExistPayloadDto(
    Long id,
    String returnIdentifier
) {
}
