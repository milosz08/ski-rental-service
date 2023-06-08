/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: FirstAccessResDto.java
 * Last modified: 6/2/23, 11:49 PM
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
import lombok.NoArgsConstructor;

import pl.polsl.skirentalservice.core.ValidatorSingleton;
import pl.polsl.skirentalservice.dto.FormValueInfoTupleDto;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Data
@NoArgsConstructor
public class FirstAccessResDto {
    private FormValueInfoTupleDto password;
    private FormValueInfoTupleDto passwordRep;
    private FormValueInfoTupleDto emailPassword;
    private FormValueInfoTupleDto emailPasswordRep;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public FirstAccessResDto(ValidatorSingleton validator, FirstAccessReqDto reqDto) {
        this.password = validator.validateField(reqDto, "password", reqDto.getPassword());
        this.passwordRep = validator.validateField(reqDto, "passwordRep", reqDto.getPasswordRep());
        this.emailPassword = validator.validateField(reqDto, "emailPassword", reqDto.getEmailPassword());
        this.emailPasswordRep = validator.validateField(reqDto, "emailPasswordRep", reqDto.getEmailPasswordRep());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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
