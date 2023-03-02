/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 * Silesian University of Technology
 *
 *  File name: ChangeForgottenPasswordReqDto.java
 *  Last modified: 21/01/2023, 14:08
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.dto.change_password;

import lombok.Data;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.NotEmpty;
import jakarta.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import pl.polsl.skirentalservice.util.Regex;
import pl.polsl.skirentalservice.core.IReqValidatePojo;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Data
public class ChangeForgottenPasswordReqDto implements IReqValidatePojo {

    @NotEmpty(message = "Pole hasła nie może być puste.")
    @Pattern(regexp = Regex.PASSWORD_REQ, message = "Nieprawidłowa wartość/wartości w polu hasło.")
    private String password;

    @NotEmpty(message = "Pole powtórzonego hasła nie może być puste.")
    @Pattern(regexp = Regex.PASSWORD_REQ, message = "Nieprawidłowa wartość/wartości w polu powtórzonego hasła.")
    private String passwordRepeat;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public ChangeForgottenPasswordReqDto(HttpServletRequest req) {
        this.password = StringUtils.trimToEmpty(req.getParameter("password"));
        this.passwordRepeat = StringUtils.trimToEmpty(req.getParameter("passwordRepeat"));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String toString() {
        return '{' +
            "password='" + password +
            ", passwordRepeat='" + passwordRepeat +
            '}';
    }
}
