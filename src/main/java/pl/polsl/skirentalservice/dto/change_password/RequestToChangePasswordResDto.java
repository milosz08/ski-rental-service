/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.dto.change_password;

import lombok.Data;
import lombok.NoArgsConstructor;
import pl.polsl.skirentalservice.core.ValidatorSingleton;
import pl.polsl.skirentalservice.dto.FormValueInfoTupleDto;

@Data
@NoArgsConstructor
public class RequestToChangePasswordResDto {
    private FormValueInfoTupleDto loginOrEmail;

    public RequestToChangePasswordResDto(ValidatorSingleton validator, RequestToChangePasswordReqDto reqDto) {
        this.loginOrEmail = validator.validateField(reqDto, "loginOrEmail", reqDto.getLoginOrEmail());
    }

    @Override
    public String toString() {
        return '{' +
            "loginOrEmail=" + loginOrEmail +
            '}';
    }
}
