/*
 * Copyright (c) 2023 by multiple authors
 * Silesian University of Technology
 *
 *  File name: AddEditEquipmentReqDto.java
 *  Last modified: 24/01/2023, 15:31
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.dto.equipment;

import lombok.Data;

import jakarta.validation.constraints.*;
import jakarta.servlet.http.HttpServletRequest;

import pl.polsl.skirentalservice.util.Gender;
import pl.polsl.skirentalservice.core.IReqValidatePojo;

import static org.apache.commons.lang3.StringUtils.trimToEmpty;

import static pl.polsl.skirentalservice.util.Regex.*;
import static pl.polsl.skirentalservice.util.Gender.findByAlias;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Data
public class AddEditEquipmentReqDto implements IReqValidatePojo {

    @NotEmpty(message = "Pole nazwy sprzętu nie może być puste.")
    @Size(min = 5, max = 50, message = "Pole nazwy sprzętu musi mieć od 5 do 50 znaków.")
    private String name;

    @NotEmpty(message = "Pole modelu sprzętu nie może być puste.")
    @Size(min = 3, max = 50, message = "Pole modelu sprzętu musi mieć od 3 do 50 znaków.")
    private String model;

    @Size(max = 200, message = "Pole opisu sprzętu może mieć maksymalnie 200 znaków.")
    private String description;

    @NotEmpty(message = "Pole stanu magazynowego sprzętu nie może być puste.")
    @Pattern(regexp = POS_NUMBER_INT, message = "Pole stanu magazynowego może przyjmować wartości od 1 do 9999.")
    private String total;

    @Pattern(regexp = DECIMAL_2_ROUND, message = "Pole rozmiaru musi być w formacie 0,00 lub 0.00, wartość min 1,00.")
    private String size;

    @NotEmpty(message = "Pole ceny za dobę wypożyczenia nie może być puste.")
    @Pattern(regexp = DECIMAL_2_ROUND, message = "Pole ceny musi być w formacie 0,00 lub 0.00, wartość min 1,00.")
    private String nettoPricePerHour;

    @NotEmpty(message = "Pole ceny za każdą kolejną godzinę wypożyczenia nie może być puste.")
    @Pattern(regexp = DECIMAL_2_ROUND, message = "Pole ceny musi być w formacie 0,00 lub 0.00, wartość min 1,00.")
    private String nettoPriceNextEveryHour;

    @NotEmpty(message = "Pole ceny za dzień wypożyczenia nie może być puste.")
    @Pattern(regexp = DECIMAL_2_ROUND, message = "Pole ceny musi być w formacie 0,00 lub 0.00, wartość min 1,00.")
    private String nettoPricePerDay;

    @NotEmpty(message = "Pole całkowitej wartości sprzętu nie może być puste.")
    @Pattern(regexp = DECIMAL_2_ROUND, message = "Pole ceny musi być w formacie 0,00 lub 0.00, wartość min 1,00.")
    private String nettoTotalValue;

    @Pattern(regexp = DEF_SELECT_VALUE, message = "Należy wybrać wartość inną niż domyślną.")
    private String type;

    @Pattern(regexp = DEF_SELECT_VALUE, message = "Należy wybrać wartość inną niż domyślną.")
    private String brand;

    @Pattern(regexp = DEF_SELECT_VALUE, message = "Należy wybrać wartość inną niż domyślną.")
    private String color;

    private Gender gender;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public AddEditEquipmentReqDto(HttpServletRequest req) {
        this.name = trimToEmpty(req.getParameter("name"));
        this.model = trimToEmpty(req.getParameter("model"));
        this.description = trimToEmpty(req.getParameter("description"));
        this.total = trimToEmpty(req.getParameter("total"));
        this.size = trimToEmpty(req.getParameter("size"));
        this.nettoPricePerHour = trimToEmpty(req.getParameter("nettoPricePerHour"));
        this.nettoPriceNextEveryHour = trimToEmpty(req.getParameter("nettoPriceNextEveryHour"));
        this.nettoPricePerDay = trimToEmpty(req.getParameter("nettoPricePerDay"));
        this.nettoTotalValue = trimToEmpty(req.getParameter("nettoTotalValue"));
        this.type = trimToEmpty(req.getParameter("type"));
        this.brand = trimToEmpty(req.getParameter("brand"));
        this.color = trimToEmpty(req.getParameter("color"));
        this.gender = findByAlias(trimToEmpty(req.getParameter("gender")));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String toString() {
        return "{" +
            "name='" + name +
            ", model='" + model +
            ", description='" + description +
            ", total='" + total +
            ", size='" + size +
            ", nettoPricePerHour='" + nettoPricePerHour +
            ", nettoPriceNextEveryHour='" + nettoPriceNextEveryHour +
            ", nettoPricePerDay='" + nettoPricePerDay +
            ", nettoTotalValue='" + nettoTotalValue +
            ", type='" + type +
            ", brand='" + brand +
            ", color='" + color +
            ", gender=" + gender +
            '}';
    }
}
