/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: RequestToChangePasswordResDto.java
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

package pl.polsl.skirentalservice.dto.change_password;

import lombok.Data;
import lombok.NoArgsConstructor;

import pl.polsl.skirentalservice.core.ValidatorSingleton;
import pl.polsl.skirentalservice.dto.FormValueInfoTupleDto;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Data
@NoArgsConstructor
public class RequestToChangePasswordResDto {
    private FormValueInfoTupleDto loginOrEmail;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public RequestToChangePasswordResDto(ValidatorSingleton validator, RequestToChangePasswordReqDto reqDto) {
        this.loginOrEmail = validator.validateField(reqDto, "loginOrEmail", reqDto.getLoginOrEmail());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String toString() {
        return '{' +
            "loginOrEmail=" + loginOrEmail +
            '}';
    }
}
