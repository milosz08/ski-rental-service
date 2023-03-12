/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 * Silesian University of Technology
 *
 *  File name: AddEditEquipmentReqDto.java
 *  Last modified: 27/01/2023, 04:04
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.dto.equipment;

import lombok.Data;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.NotEmpty;
import jakarta.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import pl.polsl.skirentalservice.util.Regex;
import pl.polsl.skirentalservice.util.Gender;
import pl.polsl.skirentalservice.core.IReqValidatePojo;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Data
@AllArgsConstructor
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
    @Pattern(regexp = Regex.POS_NUMBER_INT, message = "Pole stanu magazynowego może przyjmować wartości od 1 do 9999.")
    private String countInStore;

    @Pattern(regexp = Regex.DECIMAL_2_ROUND, message = "Pole rozmiaru musi być w formacie 0,00 lub 0.00, wartość min 1,00.")
    private String size;

    @NotEmpty(message = "Pole ceny za dobę wypożyczenia nie może być puste.")
    @Pattern(regexp = Regex.DECIMAL_2_ROUND, message = "Pole ceny musi być w formacie 0,00 lub 0.00, wartość min 1,00.")
    private String pricePerHour;

    @NotEmpty(message = "Pole ceny za każdą kolejną godzinę wypożyczenia nie może być puste.")
    @Pattern(regexp = Regex.DECIMAL_2_ROUND, message = "Pole ceny musi być w formacie 0,00 lub 0.00, wartość min 1,00.")
    private String priceForNextHour;

    @NotEmpty(message = "Pole ceny za dzień wypożyczenia nie może być puste.")
    @Pattern(regexp = Regex.DECIMAL_2_ROUND, message = "Pole ceny musi być w formacie 0,00 lub 0.00, wartość min 1,00.")
    private String pricePerDay;

    @NotEmpty(message = "Pole całkowitej wartości sprzętu nie może być puste.")
    @Pattern(regexp = Regex.DECIMAL_2_ROUND, message = "Pole ceny musi być w formacie 0,00 lub 0.00, wartość min 1,00.")
    private String valueCost;

    @Pattern(regexp = Regex.DEF_SELECT_VALUE, message = "Należy wybrać wartość inną niż domyślną.")
    private String type;

    @Pattern(regexp = Regex.DEF_SELECT_VALUE, message = "Należy wybrać wartość inną niż domyślną.")
    private String brand;

    @Pattern(regexp = Regex.DEF_SELECT_VALUE, message = "Należy wybrać wartość inną niż domyślną.")
    private String color;

    private Gender gender;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public AddEditEquipmentReqDto(HttpServletRequest req) {
        this.name = StringUtils.trimToEmpty(req.getParameter("name"));
        this.model = StringUtils.trimToEmpty(req.getParameter("model"));
        this.description = StringUtils.trimToNull(req.getParameter("description"));
        this.countInStore = StringUtils.trimToEmpty(req.getParameter("countInStore"));
        this.size = StringUtils.trimToNull(StringUtils.replaceChars(req.getParameter("size"), ',', '.'));
        this.pricePerHour = StringUtils.trimToEmpty(StringUtils.replaceChars(req.getParameter("pricePerHour"), ',', '.'));
        this.priceForNextHour = StringUtils.trimToEmpty(StringUtils.replaceChars(req.getParameter("priceForNextHour"), ',', '.'));
        this.pricePerDay = StringUtils.trimToEmpty(StringUtils.replaceChars(req.getParameter("pricePerDay"), ',', '.'));
        this.valueCost = StringUtils.trimToEmpty(StringUtils.replaceChars(req.getParameter("valueCost"), ',', '.'));
        this.type = StringUtils.trimToEmpty(req.getParameter("type"));
        this.brand = StringUtils.trimToEmpty(req.getParameter("brand"));
        this.color = StringUtils.trimToEmpty(req.getParameter("color"));
        this.gender = Gender.findByAlias(StringUtils.trimToEmpty(req.getParameter("gender")));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String toString() {
        return "{" +
            "name='" + name +
            ", model='" + model +
            ", description='" + description +
            ", countInStore='" + countInStore +
            ", size='" + size +
            ", pricePerHour='" + pricePerHour +
            ", priceForNextHour='" + priceForNextHour +
            ", pricePerDay='" + pricePerDay +
            ", valueCost='" + valueCost +
            ", type='" + type +
            ", brand='" + brand +
            ", color='" + color +
            ", gender=" + gender +
            '}';
    }
}
