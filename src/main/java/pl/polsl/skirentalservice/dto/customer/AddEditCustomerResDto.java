/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: AddEditCustomerResDto.java
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

package pl.polsl.skirentalservice.dto.customer;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import pl.polsl.skirentalservice.util.Gender;
import pl.polsl.skirentalservice.core.ValidatorSingleton;
import pl.polsl.skirentalservice.dto.FormSelectTupleDto;
import pl.polsl.skirentalservice.dto.FormValueInfoTupleDto;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Data
@NoArgsConstructor
public class AddEditCustomerResDto {
    private FormValueInfoTupleDto firstName;
    private FormValueInfoTupleDto lastName;
    private FormValueInfoTupleDto pesel;
    private FormValueInfoTupleDto phoneNumber;
    private FormValueInfoTupleDto bornDate;
    private FormValueInfoTupleDto emailAddress;
    private FormValueInfoTupleDto street;
    private FormValueInfoTupleDto buildingNr;
    private FormValueInfoTupleDto apartmentNr;
    private FormValueInfoTupleDto city;
    private FormValueInfoTupleDto postalCode;
    private List<FormSelectTupleDto> genders = Gender.getGenders();

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public AddEditCustomerResDto(ValidatorSingleton validator, AddEditCustomerReqDto reqDto) {
        this.firstName = validator.validateField(reqDto, "firstName", reqDto.getFirstName());
        this.lastName = validator.validateField(reqDto, "lastName", reqDto.getLastName());
        this.pesel = validator.validateField(reqDto, "pesel", reqDto.getPesel());
        this.phoneNumber = validator.validateField(reqDto, "phoneNumber", reqDto.getPhoneNumber());
        this.bornDate = validator.validateField(reqDto, "bornDate", reqDto.getBornDate());
        this.emailAddress = validator.validateField(reqDto, "emailAddress", reqDto.getEmailAddress());
        this.street = validator.validateField(reqDto, "street", reqDto.getStreet());
        this.buildingNr = validator.validateField(reqDto, "buildingNr", reqDto.getBuildingNr());
        this.apartmentNr = validator.validateField(reqDto, "apartmentNr", reqDto.getApartmentNr());
        this.city = validator.validateField(reqDto, "city", reqDto.getCity());
        this.postalCode = validator.validateField(reqDto, "postalCode", reqDto.getPostalCode());
        this.genders = List.of(
            Gender.MALE.convertToTuple(reqDto.getGender()),
            Gender.FEMALE.convertToTuple(reqDto.getGender())
        );
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String toString() {
        return "{" +
            "firstName=" + firstName +
            ", lastName=" + lastName +
            ", pesel=" + pesel +
            ", phoneNumber=" + phoneNumber +
            ", bornDate=" + bornDate +
            ", emailAddress=" + emailAddress +
            ", street=" + street +
            ", buildingNr=" + buildingNr +
            ", apartmentNr=" + apartmentNr +
            ", city=" + city +
            ", postalCode=" + postalCode +
            '}';
    }
}
