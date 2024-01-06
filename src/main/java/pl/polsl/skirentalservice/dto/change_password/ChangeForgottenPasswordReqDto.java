/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.dto.change_password;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import pl.polsl.skirentalservice.core.ReqValidatePojo;
import pl.polsl.skirentalservice.util.Regex;

@Data
public class ChangeForgottenPasswordReqDto implements ReqValidatePojo {
    @NotEmpty(message = "Pole hasła nie może być puste.")
    @Pattern(regexp = Regex.PASSWORD_REQ, message = "Nieprawidłowa wartość/wartości w polu hasło.")
    private String password;

    @NotEmpty(message = "Pole powtórzonego hasła nie może być puste.")
    @Pattern(regexp = Regex.PASSWORD_REQ, message = "Nieprawidłowa wartość/wartości w polu powtórzonego hasła.")
    private String passwordRepeat;

    public ChangeForgottenPasswordReqDto(HttpServletRequest req) {
        this.password = StringUtils.trimToEmpty(req.getParameter("password"));
        this.passwordRepeat = StringUtils.trimToEmpty(req.getParameter("passwordRepeat"));
    }

    @Override
    public String toString() {
        return '{' +
            "password='" + password +
            ", passwordRepeat='" + passwordRepeat +
            '}';
    }
}
