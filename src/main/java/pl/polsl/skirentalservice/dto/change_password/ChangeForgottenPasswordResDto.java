/*
 * Copyright (c) 2023 by multiple authors
 * Silesian University of Technology
 *
 *  File name: ChangeForgottenPasswordReqDto.java
 *  Last modified: 01/01/2023, 20:00
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.dto.change_password;

import lombok.*;

import pl.polsl.skirentalservice.dto.FormValueInfoTupleDto;

//----------------------------------------------------------------------------------------------------------------------

@Data
@Builder
@AllArgsConstructor
public class ChangeForgottenPasswordResDto {
    private FormValueInfoTupleDto password;
    private FormValueInfoTupleDto passwordRepeat;

    //------------------------------------------------------------------------------------------------------------------

    public ChangeForgottenPasswordResDto() {
        this.password = new FormValueInfoTupleDto();
        this.passwordRepeat = new FormValueInfoTupleDto();
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public String toString() {
        return '{' +
                "password=" + password +
                ", passwordRepeat=" + passwordRepeat +
                '}';
    }
}
