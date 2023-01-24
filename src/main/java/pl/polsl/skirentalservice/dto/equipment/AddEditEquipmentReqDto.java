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

    @Size(max = 200, message = "Pole opisu sprzętu może mieć maksymalnie 200 znaków.")
    private String description;

    @NotEmpty(message = "Pole stanu magazynowego sprzętu nie może być puste.")
    @Pattern(regexp = POS_NUMBER_INT, message = "Pole stanu magazynowego może przyjmować wartości od 1 do 9999.")
    private String total;

    @Pattern(regexp = DECIMAL_2_ROUND, message = "Pole rozmiaru musi być w formacie 0,00 lub 0.00, wartość min 1,00.")
    private String size;

    @Pattern(regexp = DEF_SELECT_VALUE, message = "Należy wybrać wartość inną niż domyślną.")
    private String type;

    @Pattern(regexp = DEF_SELECT_VALUE, message = "Należy wybrać wartość inną niż domyślną.")
    private String brand;

    @Pattern(regexp = DEF_SELECT_VALUE, message = "Należy wybrać wartość inną niż domyślną.")
    private String color;

    private Gender gender;

    // TODO: dodawnie dodatkowych pól do obsługi cen

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public AddEditEquipmentReqDto(HttpServletRequest req) {
        this.name = trimToEmpty(req.getParameter("name"));
        this.description = trimToEmpty(req.getParameter("description"));
        this.total = trimToEmpty(req.getParameter("total"));
        this.size = trimToEmpty(req.getParameter("size"));
        this.type = trimToEmpty(req.getParameter("type"));
        this.brand = trimToEmpty(req.getParameter("brand"));
        this.color = trimToEmpty(req.getParameter("color"));
        this.gender = findByAlias(trimToEmpty(req.getParameter("gender")));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // TODO: generacja metody toString()
}
