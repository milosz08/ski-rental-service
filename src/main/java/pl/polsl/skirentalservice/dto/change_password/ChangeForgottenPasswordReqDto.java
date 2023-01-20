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
import jakarta.validation.constraints.*;
import jakarta.servlet.http.HttpServletRequest;

import pl.polsl.skirentalservice.core.IReqValidatePojo;

import static org.apache.commons.lang3.StringUtils.trimToEmpty;
import static pl.polsl.skirentalservice.util.Regex.PASSWORD_REQ;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Data
@AllArgsConstructor
public class ChangeForgottenPasswordReqDto implements IReqValidatePojo {

    @NotEmpty(message = "Pole hasła nie może być puste.")
    @Pattern(regexp = PASSWORD_REQ, message = "Nieprawidłowa wartość/wartości w polu hasło.")
    private String password;

    @NotEmpty(message = "Pole powtórzonego hasła nie może być puste.")
    @Pattern(regexp = PASSWORD_REQ, message = "Nieprawidłowa wartość/wartości w polu powtórzonego hasła.")
    private String passwordRepeat;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public ChangeForgottenPasswordReqDto(HttpServletRequest req) {
        this.password = trimToEmpty(req.getParameter("password"));
        this.passwordRepeat = trimToEmpty(req.getParameter("passwordRepeat"));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String toString() {
        return '{' +
            "password='" + password + '\'' +
            ", passwordRepeat='" + passwordRepeat + '\'' +
            '}';
    }
}
