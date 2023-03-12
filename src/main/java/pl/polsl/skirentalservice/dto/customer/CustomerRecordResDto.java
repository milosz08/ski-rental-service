/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 * Silesian University of Technology
 *
 *  File name: CustomerRecordResDto.java
 *  Last modified: 22/01/2023, 17:50
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.dto.customer;

import lombok.Data;
import lombok.AllArgsConstructor;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Data
@AllArgsConstructor
public class CustomerRecordResDto {
    private Long id;
    private String fullName;
    private String email;
    private String pesel;
    private String phoneNumber;
    private String address;
}
