/*
 * Copyright (c) 2023 by multiple authors
 * Silesian University of Technology
 *
 *  File name: AddEquipmentToCartResDto.java
 *  Last modified: 28/01/2023, 21:35
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.dto.rent;

import lombok.*;

import pl.polsl.skirentalservice.dto.*;
import pl.polsl.skirentalservice.core.ValidatorBean;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Data
@NoArgsConstructor
public class AddEditEquipmentCartResDto {
    private FormValueInfoTupleDto count;
    private FormValueInfoTupleDto depositPrice;
    private FormValueInfoTupleDto description;
    private AlertTupleDto alert;
    private String immediatelyShow;
    private String eqId;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public AddEditEquipmentCartResDto(ValidatorBean validator, AddEditEquipmentCartReqDto reqDto) {
        this.count = validator.validateField(reqDto, "count", reqDto.getCount());
        this.depositPrice = validator.validateField(reqDto, "depositPrice", reqDto.getDepositPrice());
        this.description = validator.validateField(reqDto, "description", reqDto.getDescription());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void setModalImmediatelyOpen(boolean isImmediatelyOpen) {
        this.immediatelyShow = isImmediatelyOpen ? "open" : "close";
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String toString() {
        return "{" +
            "count=" + count +
            ", depositPrice=" + depositPrice +
            ", description=" + description +
            '}';
    }
}
