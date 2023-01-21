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
import jakarta.servlet.http.HttpServletRequest;

import pl.polsl.skirentalservice.core.IReqValidatePojo;

import static pl.polsl.skirentalservice.util.Regex.*;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Data
public class LoginFormReqDto implements IReqValidatePojo {

    @NotEmpty(message = "Pole loginu/adresu email nie może być puste.")
    @Pattern(regexp = LOGIN_EMAIL, message = "Nieprawidłowa wartość/wartości w polu login.")
    private String loginOrEmail;

    @NotEmpty(message = "Pole hasła nie może być puste.")
    @Pattern(regexp = PASSWORD_AVLB, message = "Nieprawidłowa wartość/wartości w polu hasło.")
    private String password;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public LoginFormReqDto(HttpServletRequest req) {
        this.loginOrEmail = trimToEmpty(req.getParameter("loginOrEmail"));
        this.password = trimToEmpty(req.getParameter("password"));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String toString() {
        return '{' +
            "loginOrEmail='" + loginOrEmail + '\'' +
            ", password='" + password + '\'' +
            '}';
    }
}
