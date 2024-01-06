/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.dto.customer;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.pl.PESEL;
import pl.polsl.skirentalservice.core.IReqValidatePojo;
import pl.polsl.skirentalservice.core.ReqValidatePojo;
import pl.polsl.skirentalservice.util.Gender;
import pl.polsl.skirentalservice.util.Regex;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Data
@AllArgsConstructor
public class AddEditCustomerReqDto implements ReqValidatePojo {
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

    public LocalDate getParsedBornDate() {
        return LocalDate.parse(bornDate, formatter);
    }

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
