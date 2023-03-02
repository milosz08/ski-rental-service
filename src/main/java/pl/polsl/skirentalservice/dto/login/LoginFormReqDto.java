/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 * Silesian University of Technology
 *
 *  File name: LoginFormReqDto.java
 *  Last modified: 21/01/2023, 14:07
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.dto.login;

import lombok.Data;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.NotEmpty;
import jakarta.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import pl.polsl.skirentalservice.util.Regex;
import pl.polsl.skirentalservice.core.IReqValidatePojo;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Data
public class LoginFormReqDto implements IReqValidatePojo {

    @NotEmpty(message = "Pole loginu/adresu email nie może być puste.")
    @Pattern(regexp = Regex.LOGIN_EMAIL, message = "Nieprawidłowa wartość/wartości w polu login.")
    private String loginOrEmail;

    @NotEmpty(message = "Pole hasła nie może być puste.")
    @Pattern(regexp = Regex.PASSWORD_AVLB, message = "Nieprawidłowa wartość/wartości w polu hasło.")
    private String password;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public LoginFormReqDto(HttpServletRequest req) {
        this.loginOrEmail = StringUtils.trimToEmpty(req.getParameter("loginOrEmail"));
        this.password = StringUtils.trimToEmpty(req.getParameter("password"));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String toString() {
        return '{' +
            "loginOrEmail='" + loginOrEmail +
            ", password='" + password +
            '}';
    }
}
