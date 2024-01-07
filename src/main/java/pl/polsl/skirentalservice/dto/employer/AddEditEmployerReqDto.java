/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.dto.employer;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.pl.PESEL;
import pl.polsl.skirentalservice.core.ConfigSingleton;
import pl.polsl.skirentalservice.core.ReqValidatePojo;
import pl.polsl.skirentalservice.exception.DateException;
import pl.polsl.skirentalservice.util.DateParser;
import pl.polsl.skirentalservice.util.Gender;
import pl.polsl.skirentalservice.util.Regex;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class AddEditEmployerReqDto implements ReqValidatePojo {
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

    @NotEmpty(message = "Pole daty zatrudnienia nie może być puste.")
    @Pattern(regexp = Regex.DATE, message = "Nieprawidłowa wartość/wartości w polu data zatrudnienia.")
    private String hiredDate;

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

    public AddEditEmployerReqDto(HttpServletRequest req) {
        this.firstName = StringUtils.trimToEmpty(req.getParameter("firstName"));
        this.lastName = StringUtils.trimToEmpty(req.getParameter("lastName"));
        this.pesel = StringUtils.trimToEmpty(req.getParameter("pesel"));
        this.phoneNumber = StringUtils.remove(StringUtils.trimToEmpty(req.getParameter("phoneNumber")), ' ');
        this.bornDate = StringUtils.trimToEmpty(req.getParameter("bornDate"));
        this.hiredDate = StringUtils.trimToEmpty(req.getParameter("hiredDate"));
        this.street = StringUtils.trimToEmpty(req.getParameter("street"));
        this.buildingNr = StringUtils.trimToEmpty(req.getParameter("buildingNr")).toLowerCase();
        this.apartmentNr = StringUtils.toRootLowerCase(StringUtils.trimToNull(req.getParameter("apartmentNr")));
        this.city = StringUtils.trimToEmpty(req.getParameter("city"));
        this.postalCode = StringUtils.trimToEmpty(req.getParameter("postalCode"));
        this.gender = Gender.findByAlias(req.getParameter("gender"));
    }

    public LocalDate getParsedBornDate() {
        return DateParser.parseToDateOnly(bornDate);
    }

    public LocalDate getParsedHiredDate() {
        return DateParser.parseToDateOnly(hiredDate);
    }

    public String getFullAddress() {
        return StringUtils.joinWith(
            StringUtils.SPACE,
            "ul.",
            street,
            buildingNr,
            StringUtils.isBlank(apartmentNr) ? StringUtils.EMPTY : "/" + apartmentNr,
            postalCode,
            city);
    }

    public void validateDates(ConfigSingleton config) {
        if (getParsedBornDate().isAfter(LocalDate.now().minusYears(config.getMaturityAge()))) {
            throw new DateException.DateInFutureException("data urodzenia", config.getMaturityAge());
        }
        if (getParsedHiredDate().isAfter(LocalDate.now())) {
            throw new DateException.DateInFutureException("data zatrudnienia");
        }
        if (getParsedBornDate().isAfter(getParsedHiredDate())) {
            throw new DateException.BornAfterHiredDateException();
        }
    }

    @Override
    public String toString() {
        return "{" +
            "firstName=" + firstName +
            ", lastName=" + lastName +
            ", pesel=" + pesel +
            ", phoneNumber=" + phoneNumber +
            ", bornDate=" + bornDate +
            ", hiredDate=" + hiredDate +
            ", street=" + street +
            ", buildingNr=" + buildingNr +
            ", apartmentNr=" + apartmentNr +
            ", city=" + city +
            ", postalCode=" + postalCode +
            ", gender=" + gender +
            '}';
    }
}
