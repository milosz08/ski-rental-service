/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 * Silesian University of Technology
 *
 *  File name: CustomerDetailsResDto.java
 *  Last modified: 09/02/2023, 00:14
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.dto.deliv_return;

import lombok.*;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Data
@AllArgsConstructor
public class CustomerDetailsResDto {
    private String fullName;
    private String pesel;
    private String phoneNumber;
    private String email;
    private String address;
}
