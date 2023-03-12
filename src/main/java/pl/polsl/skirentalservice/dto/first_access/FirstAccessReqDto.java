/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 * Silesian University of Technology
 *
 *  File name: FirstAccessReqDto.java
 *  Last modified: 21/01/2023, 16:20
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.dto.first_access;

import lombok.Data;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.NotEmpty;
import jakarta.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import pl.polsl.skirentalservice.util.Regex;
import pl.polsl.skirentalservice.core.IReqValidatePojo;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Data
public class FirstAccessReqDto implements IReqValidatePojo {

    @Pattern(regexp = Regex.PASSWORD_REQ, message = "Hasło musi zawierać co najmniej 8 znaków, jedną wielką literę, jedną " +
        "cyfrę i znak specjalny (spośród #?!@$%^&*).")
    private String password;

    @NotEmpty(message = "Pole powtórzonego hasła nie może być puste.")
    private String passwordRep;

    @Pattern(regexp = Regex.PASSWORD_REQ, message = "Hasło musi zawierać co najmniej 8 znaków, jedną wielką literę, " +
        "jedną cyfrę i znak specjalny (spośród #?!@$%^&*).")
    private String emailPassword;

    @NotEmpty(message = "Pole powtórzonego hasła nie może być puste.")
    private String emailPasswordRep;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public FirstAccessReqDto(HttpServletRequest req) {
        this.password = StringUtils.trimToEmpty(req.getParameter("password"));
        this.passwordRep = StringUtils.trimToEmpty(req.getParameter("passwordRep"));
        this.emailPassword = StringUtils.trimToEmpty(req.getParameter("emailPassword"));
        this.emailPasswordRep = StringUtils.trimToEmpty(req.getParameter("emailPasswordRep"));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String toString() {
        return "{" +
            "password='" + password +
            ", passwordRep='" + passwordRep +
            ", emailPassword='" + emailPassword +
            ", emailPasswordRep='" + emailPasswordRep +
            '}';
    }
}
