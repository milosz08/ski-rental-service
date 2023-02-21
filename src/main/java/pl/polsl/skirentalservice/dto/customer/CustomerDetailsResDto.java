/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 * Silesian University of Technology
 *
 *  File name: CustomerDetailsResDto.java
 *  Last modified: 29/01/2023, 02:20
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.dto.customer;

import pl.polsl.skirentalservice.util.Gender;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public record CustomerDetailsResDto(
    Long id,
    String fullName,
    String email,
    String bornDate,
    String pesel,
    String phoneNumber,
    Integer yearsAge,
    Gender gender,
    String cityWithPostCode,
    String address
) {
}
