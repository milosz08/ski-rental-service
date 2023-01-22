/*
 * Copyright (c) 2023 by multiple authors
 * Silesian University of Technology
 *
 *  File name: AddEmployerMailPayload.java
 *  Last modified: 21/01/2023, 16:18
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.dto.employer;

import lombok.*;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Getter
@Setter
public class AddEmployerMailPayload {
    private String fullName;
    private String pesel;
    private String phoneNumber;
    private String bornDate;
    private String hiredDate;
    private String address;
    private String gender;
    private String emailAddress;
    private String emailPassword;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public AddEmployerMailPayload(AddEditEmployerReqDto reqDto, String emailAddress, String emailPassword) {
        this.fullName = reqDto.getFirstName() + " " + reqDto.getLastName();
        this.pesel = reqDto.getPesel();
        this.phoneNumber = "+48" + reqDto.getPhoneNumber();
        this.bornDate = reqDto.getBornDate();
        this.hiredDate = reqDto.getHiredDate();
        this.address = reqDto.getFullAddress();
        this.gender = reqDto.getGender().getName();
        this.emailAddress = emailAddress;
        this.emailPassword = emailPassword;
    }
}
