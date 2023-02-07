/*
 * Copyright (c) 2023 by multiple authors
 * Silesian University of Technology
 *
 *  File name: EmailEquipmentPayloadDataDto.java
 *  Last modified: 07/02/2023, 15:05
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.dto;

import lombok.*;
import java.math.BigDecimal;

import pl.polsl.skirentalservice.entity.EquipmentEntity;
import pl.polsl.skirentalservice.dto.deliv_return.RentReturnEquipmentRecordResDto;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Data
@NoArgsConstructor
public class EmailEquipmentPayloadDataDto {
    private String count;
    private String name;
    private String typeAndModel;
    private BigDecimal totalPriceNetto;
    private BigDecimal totalPriceBrutto;
    private BigDecimal depositPriceNetto;
    private BigDecimal depositPriceBrutto;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public EmailEquipmentPayloadDataDto(EquipmentEntity eqEntity, RentReturnEquipmentRecordResDto eqDto) {
        name = eqEntity.getName();
        count = eqDto.getCount().toString();
        typeAndModel = eqEntity.getModel() + ", " + eqEntity.getEquipmentType().getName();
        depositPriceNetto = eqDto.getDepositPriceNetto();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String toString() {
        return "{" +
            "count='" + count +
            ", name='" + name +
            ", typeAndModel='" + typeAndModel +
            ", totalPriceNetto=" + totalPriceNetto +
            ", totalPriceBrutto=" + totalPriceBrutto +
            ", depositPriceNetto=" + depositPriceNetto +
            ", depositPriceBrutto=" + depositPriceBrutto +
            '}';
    }
}
