/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 * Silesian University of Technology
 *
 *  File name: NewRentDetailsReqDto.java
 *  Last modified: 29/01/2023, 12:44
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.dto.rent;

import lombok.Data;

import jakarta.validation.constraints.*;
import jakarta.servlet.http.HttpServletRequest;

import pl.polsl.skirentalservice.core.IReqValidatePojo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static java.time.LocalDateTime.parse;
import static org.apache.commons.lang3.StringUtils.*;
import static pl.polsl.skirentalservice.util.Regex.*;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Data
public class NewRentDetailsReqDto implements IReqValidatePojo {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @NotEmpty(message = "Pole daty wypożyczenia nie może być puste.")
    @Pattern(regexp = DATE_TIME, message = "Nieprawidłowa wartość w polu data wypożyczenia.")
    private String rentDateTime;

    @NotEmpty(message = "Pole daty zwrotu wypożyczenia nie może być puste.")
    @Pattern(regexp = DATE_TIME, message = "Nieprawidłowa wartość w polu data zwrotu wypożyczenia.")
    private String returnDateTime;

    @NotEmpty(message = "Pole wartości procentowej podatku nie może być puste.")
    @Pattern(regexp = TAX, message = "Nieprawidłowa wartość w polu wartości procentowej podatku.")
    private String tax;

    @Size(max = 200, message = "Pole dodatkowych uwag do składanego wypożyczenia może mieć maksymalnie 200 znaków.")
    private String description;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public NewRentDetailsReqDto(HttpServletRequest req) {
        this.rentDateTime = trimToEmpty(req.getParameter("rentDateTime"));
        this.returnDateTime = trimToEmpty(req.getParameter("returnDateTime"));
        this.tax = trimToEmpty(req.getParameter("tax")).replace(',', '.');
        this.description = trimToNull(req.getParameter("description"));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public LocalDateTime getParsedRentDateTime() {
        return parse(rentDateTime.replace('T', ' '), formatter);
    }

    public LocalDateTime getParsedReturnDateTime() {
        return parse(returnDateTime.replace('T', ' '), formatter);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String toString() {
        return "{" +
            "rentDateTime='" + rentDateTime +
            ", returnDateTime='" + returnDateTime +
            ", tax='" + tax +
            ", description='" + description +
            '}';
    }
}
