/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.dto.first_access;

import lombok.Data;
import lombok.NoArgsConstructor;
import pl.polsl.skirentalservice.core.ValidatorBean;
import pl.polsl.skirentalservice.dto.FormValueInfoTupleDto;

@Data
@NoArgsConstructor
public class FirstAccessResDto {
    private FormValueInfoTupleDto password;
    private FormValueInfoTupleDto passwordRep;
    private FormValueInfoTupleDto emailPassword;
    private FormValueInfoTupleDto emailPasswordRep;

    public FirstAccessResDto(ValidatorBean validator, FirstAccessReqDto reqDto) {
        this.password = validator.validateField(reqDto, "password", reqDto.getPassword());
        this.passwordRep = validator.validateField(reqDto, "passwordRep", reqDto.getPasswordRep());
        this.emailPassword = validator.validateField(reqDto, "emailPassword", reqDto.getEmailPassword());
        this.emailPasswordRep = validator.validateField(reqDto, "emailPasswordRep", reqDto.getEmailPasswordRep());
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
