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

import jakarta.validation.constraints.*;
import jakarta.servlet.http.HttpServletRequest;

import pl.polsl.skirentalservice.core.IReqValidatePojo;

import static org.apache.commons.lang3.StringUtils.trimToEmpty;
import static pl.polsl.skirentalservice.util.Regex.PASSWORD_REQ;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Data
public class FirstAccessReqDto implements IReqValidatePojo {

    @Pattern(regexp = PASSWORD_REQ, message = "Hasło musi zawierać co najmniej 8 znaków, jedną wielką literę, jedną " +
        "cyfrę i znak specjalny (spośród #?!@$%^&*).")
    private String password;

    @NotEmpty(message = "Pole powtórzonego hasła nie może być puste.")
    private String passwordRep;

    @Pattern(regexp = PASSWORD_REQ, message = "Hasło musi zawierać co najmniej 8 znaków, jedną wielką literę, " +
        "jedną cyfrę i znak specjalny (spośród #?!@$%^&*).")
    private String emailPassword;

    @NotEmpty(message = "Pole powtórzonego hasła nie może być puste.")
    private String emailPasswordRep;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public FirstAccessReqDto(HttpServletRequest req) {
        this.password = trimToEmpty(req.getParameter("password"));
        this.passwordRep = trimToEmpty(req.getParameter("passwordRep"));
        this.emailPassword = trimToEmpty(req.getParameter("emailPassword"));
        this.emailPasswordRep = trimToEmpty(req.getParameter("emailPasswordRep"));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String toString() {
        return "{" +
            "password='" + password + '\'' +
            ", passwordRep='" + passwordRep + '\'' +
            ", emailPassword='" + emailPassword + '\'' +
            ", emailPasswordRep='" + emailPasswordRep + '\'' +
            '}';
    }
}
