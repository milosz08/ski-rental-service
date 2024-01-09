/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.dto.login;

import lombok.Data;
import lombok.NoArgsConstructor;
import pl.polsl.skirentalservice.core.ValidatorBean;
import pl.polsl.skirentalservice.dto.FormValueInfoTupleDto;

@Data
@NoArgsConstructor
public class LoginFormResDto {
    private FormValueInfoTupleDto loginOrEmail;
    private FormValueInfoTupleDto password;

    public LoginFormResDto(ValidatorBean validator, LoginFormReqDto reqDto) {
        this.loginOrEmail = validator.validateField(reqDto, "loginOrEmail", reqDto.getLoginOrEmail());
        this.password = validator.validateField(reqDto, "password", reqDto.getPassword());
    }

    @Override
    public String toString() {
        return '{' +
            "loginOrEmail=" + loginOrEmail +
            ", password=" + password +
            '}';
    }
}
