/*
 * Copyright (c) 2023 by multiple authors
 * Silesian University of Technology
 *
 *  File name: AddEditEmployerReqDto.java
 *  Last modified: 21/01/2023, 16:18
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.dto.employer;

import lombok.*;
import org.hibernate.validator.constraints.pl.PESEL;

import jakarta.validation.constraints.*;
import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import pl.polsl.skirentalservice.core.*;
import pl.polsl.skirentalservice.util.Gender;
import pl.polsl.skirentalservice.exception.DateException;

import static java.time.LocalDate.parse;
import static java.util.Objects.requireNonNullElse;
import static org.apache.commons.lang3.StringUtils.*;

import static pl.polsl.skirentalservice.util.Regex.*;
import static pl.polsl.skirentalservice.util.Gender.findByAlias;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Data
@AllArgsConstructor
public class AddEditEmployerReqDto implements IReqValidatePojo {

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

    @NotEmpty(message = "Pole daty zatrudnienia nie może być puste.")
    @Pattern(regexp = DATE, message = "Nieprawidłowa wartość/wartości w polu data zatrudnienia.")
    private String hiredDate;

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

    public AddEditEmployerReqDto(HttpServletRequest req) {
        this.firstName = trimToEmpty(req.getParameter("firstName"));
        this.lastName = trimToEmpty(req.getParameter("lastName"));
        this.pesel = trimToEmpty(req.getParameter("pesel"));
        this.phoneNumber = remove(trimToEmpty(req.getParameter("phoneNumber")), ' ');
        this.bornDate = trimToEmpty(req.getParameter("bornDate"));
        this.hiredDate = trimToEmpty(req.getParameter("hiredDate"));
        this.street = trimToEmpty(req.getParameter("street"));
        this.buildingNr = trimToEmpty(req.getParameter("buildingNr"));
        this.apartmentNr = trimToNull(req.getParameter("apartmentNr"));
        this.city = trimToNull(req.getParameter("city"));
        this.postalCode = trimToNull(req.getParameter("postalCode"));
        this.gender = findByAlias(requireNonNullElse(req.getParameter("gender"), "M"));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public LocalDate getParsedBornDate() {
        return parse(bornDate, formatter);
    }

    public LocalDate getParsedHiredDate() {
        return parse(hiredDate, formatter);
    }

    public String getFullAddress() {
        return joinWith(" ", "ul.", street, buildingNr, isBlank(apartmentNr) ? "" : "/" + apartmentNr, postalCode, city);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void validateDates(ConfigBean config) {
        if (getParsedBornDate().isAfter(LocalDate.now().minusYears(config.getCircaDateYears()))) {
            throw new DateException.DateInFutureException("data urodzenia", config.getCircaDateYears());
        }
        if (getParsedHiredDate().isAfter(LocalDate.now())) {
            throw new DateException.DateInFutureException("data zatrudnienia");
        }
        if (getParsedBornDate().isAfter(getParsedHiredDate())) {
            throw new DateException.BornAfterHiredDateException();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String toString() {
        return "{" +
            "firstName='" + firstName + '\'' +
            ", lastName='" + lastName + '\'' +
            ", pesel='" + pesel + '\'' +
            ", phoneNumber='" + phoneNumber + '\'' +
            ", bornDate='" + bornDate + '\'' +
            ", hiredDate='" + hiredDate + '\'' +
            ", street='" + street + '\'' +
            ", buildingNr='" + buildingNr + '\'' +
            ", apartmentNr='" + apartmentNr + '\'' +
            ", city='" + city + '\'' +
            ", postalCode='" + postalCode + '\'' +
            ", gender=" + gender +
            '}';
    }
}
