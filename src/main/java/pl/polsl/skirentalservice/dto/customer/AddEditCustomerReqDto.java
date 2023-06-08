/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: AddEditCustomerReqDto.java
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

package pl.polsl.skirentalservice.dto.customer;

import lombok.Data;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.NotEmpty;
import jakarta.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.pl.PESEL;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import pl.polsl.skirentalservice.util.Regex;
import pl.polsl.skirentalservice.util.Gender;
import pl.polsl.skirentalservice.core.IReqValidatePojo;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Data
@AllArgsConstructor
public class AddEditCustomerReqDto implements IReqValidatePojo {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @NotEmpty(message = "Pole imienia nie może być puste.")
    @Pattern(regexp = Regex.NAME_SURNAME, message = "Nieprawidłowa wartość/wartości w polu imię.")
    private String firstName;

    @NotEmpty(message = "Pole nazwiska nie może być puste.")
    @Pattern(regexp = Regex.NAME_SURNAME, message = "Nieprawidłowa wartość/wartości w polu nazwisko.")
    private String lastName;

    @NotEmpty(message = "Pole PESEL nie może być puste.")
    @PESEL(message = "Nieprawidłowa wartość/wartości w polu PESEL.")
    private String pesel;

    @NotEmpty(message = "Pole nr telefonu nie może być puste.")
    @Pattern(regexp = Regex.PHONE_NUMBER, message = "Nieprawidłowa wartość/wartości w polu nr telefonu.")
    private String phoneNumber;

    @NotEmpty(message = "Pole daty urodzenia nie może być puste.")
    @Pattern(regexp = Regex.DATE, message = "Nieprawidłowa wartość/wartości w polu data urodzenia.")
    private String bornDate;

    @NotEmpty(message = "Pole adresu email nie może być puste.")
    @Email(message = "Nieprawidłowa wartość/wartości w polu adres email.")
    private String emailAddress;

    @NotEmpty(message = "Pole ulicy zamieszkania nie może być puste.")
    @Pattern(regexp = Regex.STREET, message = "Nieprawidłowa wartość/wartości w polu ulica zamieszkania.")
    private String street;

    @NotEmpty(message = "Pole nr budynku zamieszkania nie może być puste.")
    @Pattern(regexp = Regex.BUILDING_NR, message = "Nieprawidłowa wartość/wartości w polu nr budynku.")
    private String buildingNr;

    @Pattern(regexp = Regex.APARTMENT_NR, message = "Nieprawidłowa wartość/wartości w polu nr mieszkania.")
    private String apartmentNr;

    @NotEmpty(message = "Pole miasto zamieszkania nie może być puste.")
    @Pattern(regexp = Regex.CITY, message = "Nieprawidłowa wartość/wartości w polu miasto zamieszkania.")
    private String city;

    @NotEmpty(message = "Pole kodu pocztowego nie może być puste.")
    @Pattern(regexp = Regex.POSTAL_CODE, message = "Nieprawidłowa wartość/wartości w polu kod pocztowy.")
    private String postalCode;

    private Gender gender;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public AddEditCustomerReqDto(HttpServletRequest req) {
        this.firstName = StringUtils.trimToEmpty(req.getParameter("firstName"));
        this.lastName = StringUtils.trimToEmpty(req.getParameter("lastName"));
        this.pesel = StringUtils.trimToEmpty(req.getParameter("pesel"));
        this.phoneNumber = StringUtils.remove(StringUtils.trimToEmpty(req.getParameter("phoneNumber")), ' ');
        this.bornDate = StringUtils.trimToEmpty(req.getParameter("bornDate"));
        this.emailAddress = StringUtils.trimToEmpty(req.getParameter("emailAddress"));
        this.street = StringUtils.trimToEmpty(req.getParameter("street"));
        this.buildingNr = StringUtils.trimToEmpty(req.getParameter("buildingNr")).toLowerCase();
        this.apartmentNr = StringUtils.toRootLowerCase(StringUtils.trimToNull(req.getParameter("apartmentNr")));
        this.city = StringUtils.trimToEmpty(req.getParameter("city"));
        this.postalCode = StringUtils.trimToEmpty(req.getParameter("postalCode"));
        this.gender = Gender.findByAlias(req.getParameter("gender"));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public LocalDate getParsedBornDate() {
        return LocalDate.parse(bornDate, formatter);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String toString() {
        return "{" +
            "firstName='" + firstName +
            ", lastName='" + lastName +
            ", pesel='" + pesel +
            ", phoneNumber='" + phoneNumber +
            ", bornDate='" + bornDate +
            ", emailAddress='" + emailAddress +
            ", street='" + street +
            ", buildingNr='" + buildingNr +
            ", apartmentNr='" + apartmentNr +
            ", city='" + city +
            ", postalCode='" + postalCode +
            '}';
    }
}
