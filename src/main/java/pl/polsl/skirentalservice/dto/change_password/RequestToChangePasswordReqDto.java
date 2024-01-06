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
public class RequestToChangePasswordReqDto implements ReqValidatePojo {
    @NotEmpty(message = "Pole loginu/adresu email nie może być puste.")
    @Pattern(regexp = Regex.LOGIN_EMAIL, message = "Nieprawidłowa wartość/wartości w polu login/adres email.")
    private String loginOrEmail;

    public RequestToChangePasswordReqDto(HttpServletRequest req) {
        this.loginOrEmail = StringUtils.trimToEmpty(req.getParameter("loginOrEmail"));
    }

    @Override
    public String toString() {
        return '{' +
            "loginOrEmail=" + loginOrEmail +
            '}';
    }
}
