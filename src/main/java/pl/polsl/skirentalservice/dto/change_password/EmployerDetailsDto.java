/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.dto.change_password;

public record EmployerDetailsDto(
    Long id,
    String login,
    String fullName,
    String emailAddress
) {
}
