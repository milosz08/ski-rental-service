/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 * Silesian University of Technology
 *
 *  File name: EmployerDetailsResDto.java
 *  Last modified: 29/01/2023, 02:20
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.dto.employer;

import pl.polsl.skirentalservice.util.Gender;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public record EmployerDetailsResDto(
    Long id,
    String fullName,
    String login,
    String email,
    String bornDate,
    String hiredDate,
    String pesel,
    String phoneNumber,
    Integer yearsAge,
    Integer yearsHired,
    Gender gender,
    String cityWithPostCode,
    String accountState,
    String accountStateColor,
    String address
) {
}
