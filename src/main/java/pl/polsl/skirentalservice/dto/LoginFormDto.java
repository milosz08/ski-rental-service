/*
 * Copyright (c) 2022 by multiple authors
 * Silesian University of Technology
 *
 *  File name: LoginFormDto.java
 *  Last modified: 25.12.2022, 02:38
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.dto;

import lombok.*;
import static pl.polsl.skirentalservice.util.AlertType.INACTIVE;

//----------------------------------------------------------------------------------------------------------------------

@Data
@Builder
@AllArgsConstructor
public class LoginFormDto {
    private AlertTupleDto banner;
    private FormValueInfoTupleDto login;
    private FormValueInfoTupleDto password;

    //------------------------------------------------------------------------------------------------------------------

    public LoginFormDto() {
        this.banner = new AlertTupleDto(false, "", INACTIVE);
        this.login = new FormValueInfoTupleDto();
        this.password = new FormValueInfoTupleDto();
    }
}
