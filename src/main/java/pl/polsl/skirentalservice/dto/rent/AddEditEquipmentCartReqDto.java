/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 * Silesian University of Technology
 *
 *  File name: AddEditEquipmentCartReqDto.java
 *  Last modified: 29/01/2023, 20:12
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

import static pl.polsl.skirentalservice.util.Regex.*;
import static org.apache.commons.lang3.StringUtils.*;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Data
public class AddEditEquipmentCartReqDto implements IReqValidatePojo {

    @NotEmpty(message = "Pole ilości sprzętów w wypożyczeniu nie może być puste.")
    @Pattern(regexp = POS_NUMBER_INT, message = "Pole ilości sprzętów w wypożyczeniu może przyjmować wartości od 1 do 9999.")
    private String count;

    @Pattern(regexp = DECIMAL_2_ROUND, message = "Pole kaucji musi być w formacie 0,00 lub 0.00, wartość min 1,00.")
    private String depositPrice;

    @Size(max = 200, message = "Pole dodatkowych uwag może mieć maksymalnie 200 znaków.")
    private String description;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public AddEditEquipmentCartReqDto(HttpServletRequest req) {
        this.count = trimToEmpty(req.getParameter("count"));
        this.depositPrice = trimToEmpty(req.getParameter("depositPrice")).replace(',', '.');
        this.description = trimToNull(req.getParameter("description"));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String toString() {
        return "{" +
            "count='" + count +
            ", depositPrice='" + depositPrice +
            ", description='" + description +
            '}';
    }
}
