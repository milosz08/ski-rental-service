/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: FirstAccessReqDto.java
 * Last modified: 3/12/23, 11:01 AM
 * Project name: ski-rental-service
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 *
 *     <http://www.apache.org/license/LICENSE-2.0>
 *
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the license.
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
