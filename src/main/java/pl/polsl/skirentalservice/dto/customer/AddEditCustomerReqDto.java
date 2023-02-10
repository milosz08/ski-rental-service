/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 * Silesian University of Technology
 *
 *  File name: AddEditCustomerReqDto.java
 *  Last modified: 06/02/2023, 19:56
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.dto.customer;

import lombok.*;
import org.hibernate.validator.constraints.pl.PESEL;

import jakarta.validation.constraints.*;
import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import pl.polsl.skirentalservice.util.Gender;
import pl.polsl.skirentalservice.core.IReqValidatePojo;

import static java.time.LocalDate.parse;
import static org.apache.commons.lang3.StringUtils.*;

import static pl.polsl.skirentalservice.util.Regex.*;
import static pl.polsl.skirentalservice.util.Gender.findByAlias;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Data
@AllArgsConstructor
public class AddEditCustomerReqDto implements IReqValidatePojo {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @NotEmpty(message = "Pole imienia nie może być puste.")
    @Pattern(regexp = NAME_SURNAME, message = "Nieprawidłowa wartość/wartości w polu imię.")
    private String firstName;

    @NotEmpty(message = "Pole nazwiska nie może być puste.")
    @Pattern(regexp = NAME_SURNAME, message = "Nieprawidłowa wartość/wartości w polu nazwisko.")
    private String lastName;

    @NotEmpty(message = "Pole PESEL nie może być puste.")
    @PESEL(message = "Nieprawidłowa wartość/wartości w polu PESEL.")
    private String pesel;

    @NotEmpty(message = "Pole nr telefonu nie może być puste.")
    @Pattern(regexp = PHONE_NUMBER, message = "Nieprawidłowa wartość/wartości w polu nr telefonu.")
    private String phoneNumber;

    @NotEmpty(message = "Pole daty urodzenia nie może być puste.")
    @Pattern(regexp = DATE, message = "Nieprawidłowa wartość/wartości w polu data urodzenia.")
    private String bornDate;

    @NotEmpty(message = "Pole adresu email nie może być puste.")
    @Email(message = "Nieprawidłowa wartość/wartości w polu adres email.")
    private String emailAddress;

    @NotEmpty(message = "Pole ulicy zamieszkania nie może być puste.")
    @Pattern(regexp = STREET, message = "Nieprawidłowa wartość/wartości w polu ulica zamieszkania.")
    private String street;

    @NotEmpty(message = "Pole nr budynku zamieszkania nie może być puste.")
    @Pattern(regexp = BUILDING_NR, message = "Nieprawidłowa wartość/wartości w polu nr budynku.")
    private String buildingNr;

    @Pattern(regexp = APARTMENT_NR, message = "Nieprawidłowa wartość/wartości w polu nr mieszkania.")
    private String apartmentNr;

    @NotEmpty(message = "Pole miasto zamieszkania nie może być puste.")
    @Pattern(regexp = CITY, message = "Nieprawidłowa wartość/wartości w polu miasto zamieszkania.")
    private String city;

    @NotEmpty(message = "Pole kodu pocztowego nie może być puste.")
    @Pattern(regexp = POSTAL_CODE, message = "Nieprawidłowa wartość/wartości w polu kod pocztowy.")
    private String postalCode;

    private Gender gender;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public AddEditCustomerReqDto(HttpServletRequest req) {
        this.firstName = trimToEmpty(req.getParameter("firstName"));
        this.lastName = trimToEmpty(req.getParameter("lastName"));
        this.pesel = trimToEmpty(req.getParameter("pesel"));
        this.phoneNumber = remove(trimToEmpty(req.getParameter("phoneNumber")), ' ');
        this.bornDate = trimToEmpty(req.getParameter("bornDate"));
        this.emailAddress = trimToEmpty(req.getParameter("emailAddress"));
        this.street = trimToEmpty(req.getParameter("street"));
        this.buildingNr = trimToEmpty(req.getParameter("buildingNr")).toLowerCase();
        this.apartmentNr = trimToNull(req.getParameter("apartmentNr")).toLowerCase();
        this.city = trimToEmpty(req.getParameter("city"));
        this.postalCode = trimToEmpty(req.getParameter("postalCode"));
        this.gender = findByAlias(req.getParameter("gender"));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public LocalDate getParsedBornDate() {
        return parse(bornDate, formatter);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String toString() {
        return "{" +
            "firstName='" + firstName +
            ", lastName='" + lastName +
            ", pesel='" + pesel +
            ", phoneNumber='" + phoneNumber +
            ", bornDate='" + bornDate + '\'' +
            ", emailAddress='" + emailAddress +
            ", street='" + street +
            ", buildingNr='" + buildingNr +
            ", apartmentNr='" + apartmentNr +
            ", city='" + city +
            ", postalCode='" + postalCode +
            '}';
    }
}
