/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: AddEmployerMailPayload.java
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

package pl.polsl.skirentalservice.dto.employer;

import lombok.Getter;
import lombok.Setter;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Getter
@Setter
public class AddEmployerMailPayload {
    private String fullName;
    private String pesel;
    private String phoneNumber;
    private String bornDate;
    private String hiredDate;
    private String address;
    private String gender;
    private String emailAddress;
    private String emailPassword;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public AddEmployerMailPayload(AddEditEmployerReqDto reqDto, String emailAddress, String emailPassword) {
        this.fullName = reqDto.getFirstName() + " " + reqDto.getLastName();
        this.pesel = reqDto.getPesel();
        this.phoneNumber = "+48" + reqDto.getPhoneNumber();
        this.bornDate = reqDto.getBornDate();
        this.hiredDate = reqDto.getHiredDate();
        this.address = reqDto.getFullAddress();
        this.gender = reqDto.getGender().getName();
        this.emailAddress = emailAddress;
        this.emailPassword = emailPassword;
    }
}
