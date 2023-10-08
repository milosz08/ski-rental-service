/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.dto.login;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import pl.polsl.skirentalservice.core.IReqValidatePojo;
import pl.polsl.skirentalservice.util.Regex;

@Data
public class LoginFormReqDto implements IReqValidatePojo {

    @NotEmpty(message = "Pole loginu/adresu email nie może być puste.")
    @Pattern(regexp = Regex.LOGIN_EMAIL, message = "Nieprawidłowa wartość/wartości w polu login.")
    private String loginOrEmail;

    @NotEmpty(message = "Pole hasła nie może być puste.")
    @Pattern(regexp = Regex.PASSWORD_AVLB, message = "Nieprawidłowa wartość/wartości w polu hasło.")
    private String password;

    public LoginFormReqDto(HttpServletRequest req) {
        this.loginOrEmail = StringUtils.trimToEmpty(req.getParameter("loginOrEmail"));
        this.password = StringUtils.trimToEmpty(req.getParameter("password"));
    }

    @Override
    public String toString() {
        return '{' +
            "loginOrEmail='" + loginOrEmail +
            ", password='" + password +
            '}';
    }
}
