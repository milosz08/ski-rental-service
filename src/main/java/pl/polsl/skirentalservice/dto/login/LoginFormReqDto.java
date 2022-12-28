/*
 * Copyright (c) 2022 by multiple authors
 * Silesian University of Technology
 *
 *  File name: LoginFromDtoReq.java
 *  Last modified: 27/12/2022, 22:24
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.dto.login;

import lombok.*;
import jakarta.validation.constraints.*;

import java.io.Serializable;

import pl.polsl.skirentalservice.core.IReqValidatePojo;

//----------------------------------------------------------------------------------------------------------------------

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginFormReqDto implements Serializable, IReqValidatePojo {
    private static final long serialVersionUID = 1L;

    @NotEmpty(message = "Pole loginu nie może być puste.")
    @Pattern(regexp = "^[a-z0-9]{5,20}$", message = "Nieprawidłowa wartość/wartości w polu login.")
    private String login;

    @NotEmpty(message = "Pole hasła nie może być puste.")
    @Pattern(regexp = "^[a-zA-Z0-9@#$%&*]{8,30}$", message = "Nieprawidłowa wartość/wartości w polu hasło.")
    private String password;

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public String toString() {
        return "{" +
                "login='" + login + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
