/*
 * Copyright (c) 2023 by multiple authors
 * Silesian University of Technology
 *
 *  File name: CustomerDetailsResDto.java
 *  Last modified: 22/01/2023, 23:23
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.dto.customer;

import lombok.*;
import pl.polsl.skirentalservice.util.Gender;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Data
@AllArgsConstructor
public class CustomerDetailsResDto {
    private Long id;
    private String fullName;
    private String email;
    private String bornDate;
    private String pesel;
    private String phoneNumber;
    private Integer yearsAge;
    private Gender gender;
    private String cityWithPostCode;
    private String address;
}
