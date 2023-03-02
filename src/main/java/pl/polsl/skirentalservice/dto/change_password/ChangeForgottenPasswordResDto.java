/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 * Silesian University of Technology
 *
 *  File name: ChangeForgottenPasswordResDto.java
 *  Last modified: 20/01/2023, 06:33
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.dto.change_password;

import lombok.Data;
import lombok.NoArgsConstructor;

import pl.polsl.skirentalservice.core.ValidatorBean;
import pl.polsl.skirentalservice.dto.FormValueInfoTupleDto;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Data
@NoArgsConstructor
public class ChangeForgottenPasswordResDto {
    private FormValueInfoTupleDto password;
    private FormValueInfoTupleDto passwordRepeat;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public ChangeForgottenPasswordResDto(ValidatorBean validator, ChangeForgottenPasswordReqDto reqDto) {
        this.password = validator.validateField(reqDto, "password", reqDto.getPassword());
        this.passwordRepeat = validator.validateField(reqDto, "passwordRepeat", reqDto.getPasswordRepeat());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String toString() {
        return '{' +
            "password=" + password +
            ", passwordRepeat=" + passwordRepeat +
            '}';
    }
}
