/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 * Silesian University of Technology
 *
 *  File name: RequestToChangePasswordReqDto.java
 *  Last modified: 27/01/2023, 16:44
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
public class RequestToChangePasswordReqDto implements IReqValidatePojo {

    @NotEmpty(message = "Pole loginu/adresu email nie może być puste.")
    @Pattern(regexp = Regex.LOGIN_EMAIL, message = "Nieprawidłowa wartość/wartości w polu login/adres email.")
    private String loginOrEmail;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public RequestToChangePasswordReqDto(HttpServletRequest req) {
        this.loginOrEmail = StringUtils.trimToEmpty(req.getParameter("loginOrEmail"));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String toString() {
        return '{' +
            "loginOrEmail='" + loginOrEmail +
            '}';
    }
}
