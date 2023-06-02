/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 * Silesian University of Technology
 *
 *  File name: FirstAccessResDto.java
 *  Last modified: 21/01/2023, 16:20
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.dto.first_access;

import lombok.Data;
import lombok.NoArgsConstructor;

import pl.polsl.skirentalservice.core.ValidatorSingleton;
import pl.polsl.skirentalservice.dto.FormValueInfoTupleDto;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Data
@NoArgsConstructor
public class FirstAccessResDto {
    private FormValueInfoTupleDto password;
    private FormValueInfoTupleDto passwordRep;
    private FormValueInfoTupleDto emailPassword;
    private FormValueInfoTupleDto emailPasswordRep;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public FirstAccessResDto(ValidatorSingleton validator, FirstAccessReqDto reqDto) {
        this.password = validator.validateField(reqDto, "password", reqDto.getPassword());
        this.passwordRep = validator.validateField(reqDto, "passwordRep", reqDto.getPasswordRep());
        this.emailPassword = validator.validateField(reqDto, "emailPassword", reqDto.getEmailPassword());
        this.emailPasswordRep = validator.validateField(reqDto, "emailPasswordRep", reqDto.getEmailPasswordRep());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String toString() {
        return "{" +
            "password=" + password +
            ", passwordRep=" + passwordRep +
            ", emailPassword=" + emailPassword +
            ", emailPasswordRep=" + emailPasswordRep +
            '}';
    }
}
