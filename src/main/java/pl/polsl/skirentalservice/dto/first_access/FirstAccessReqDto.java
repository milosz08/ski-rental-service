/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.dto.first_access;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import pl.polsl.skirentalservice.core.ReqValidatePojo;
import pl.polsl.skirentalservice.core.servlet.WebServletRequest;
import pl.polsl.skirentalservice.util.Regex;

@Data
public class FirstAccessReqDto implements ReqValidatePojo {
    @Pattern(
        regexp = Regex.PASSWORD_REQ,
        message = "Hasło musi zawierać co najmniej 8 znaków, jedną wielką literę, " +
            "jedną cyfrę i znak specjalny (spośród #?!@$%^&*).")
    private String password;

    @NotEmpty(message = "Pole powtórzonego hasła nie może być puste.")
    private String passwordRep;

    @Pattern(
        regexp = Regex.PASSWORD_REQ,
        message = "Hasło musi zawierać co najmniej 8 znaków, jedną wielką literę, " +
            "jedną cyfrę i znak specjalny (spośród #?!@$%^&*).")
    private String emailPassword;

    @NotEmpty(message = "Pole powtórzonego hasła nie może być puste.")
    private String emailPasswordRep;

    public FirstAccessReqDto(WebServletRequest req) {
        this.password = StringUtils.trimToEmpty(req.getParameter("password"));
        this.passwordRep = StringUtils.trimToEmpty(req.getParameter("passwordRep"));
        this.emailPassword = StringUtils.trimToEmpty(req.getParameter("emailPassword"));
        this.emailPasswordRep = StringUtils.trimToEmpty(req.getParameter("emailPasswordRep"));
    }

    @Override
    public String toString() {
        return "{" +
            "password=" + password +
            ", passwordRep=" + passwordRep +
            ", emailPassword=" + emailPassword +
            ", emailPasswordRep=" + emailPasswordRep +
            '}';
    }
}
