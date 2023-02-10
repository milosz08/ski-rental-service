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

import lombok.*;
import pl.polsl.skirentalservice.util.Gender;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Data
@AllArgsConstructor
public class EmployerDetailsResDto {
    private Long id;
    private String fullName;
    private String login;
    private String email;
    private String bornDate;
    private String hiredDate;
    private String pesel;
    private String phoneNumber;
    private Integer yearsAge;
    private Integer yearsHired;
    private Gender gender;
    private String cityWithPostCode;
    private String accountState;
    private String accountStateColor;
    private String address;
}
