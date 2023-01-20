/*
 * Copyright (c) 2023 by multiple authors
 * Silesian University of Technology
 *
 *  File name: RequestToChangePasswordResDto.java
 *  Last modified: 01/01/2023, 00:45
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.dto.change_password;

import lombok.*;

import pl.polsl.skirentalservice.dto.FormValueInfoTupleDto;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Data
@AllArgsConstructor
public class RequestToChangePasswordResDto {
    private FormValueInfoTupleDto loginOrEmail;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public RequestToChangePasswordResDto() {
        this.loginOrEmail = new FormValueInfoTupleDto();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String toString() {
        return '{' +
            "loginOrEmail=" + loginOrEmail +
            '}';
    }
}
