/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.dto.rent;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import pl.polsl.skirentalservice.core.IReqValidatePojo;
import pl.polsl.skirentalservice.util.Regex;

@Data
public class AddEditEquipmentCartReqDto implements IReqValidatePojo {

    @NotEmpty(message = "Pole ilości sprzętów w wypożyczeniu nie może być puste.")
    @Pattern(regexp = Regex.POS_NUMBER_INT, message = "Pole ilości sprzętów w wypożyczeniu może przyjmować wartości od 1 do 9999.")
    private String count;

    @Pattern(regexp = Regex.DECIMAL_2_ROUND, message = "Pole kaucji musi być w formacie 0,00 lub 0.00, wartość min 1,00.")
    private String depositPrice;

    @Size(max = 200, message = "Pole dodatkowych uwag może mieć maksymalnie 200 znaków.")
    private String description;

    public AddEditEquipmentCartReqDto(HttpServletRequest req) {
        this.count = StringUtils.trimToEmpty(req.getParameter("count"));
        this.depositPrice = StringUtils.trimToEmpty(req.getParameter("depositPrice")).replace(',', '.');
        this.description = StringUtils.trimToNull(req.getParameter("description"));
    }

    @Override
    public String toString() {
        return "{" +
            "count='" + count +
            ", depositPrice='" + depositPrice +
            ", description='" + description +
            '}';
    }
}
