/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 * Silesian University of Technology
 *
 *  File name: EmployerDetailsDto.java
 *  Last modified: 20/01/2023, 06:33
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.dto.change_password;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public record EmployerDetailsDto(
    Long id,
    String login,
    String fullName,
    String emailAddress
) {
}
