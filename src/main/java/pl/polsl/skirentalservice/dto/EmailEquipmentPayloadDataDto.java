/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import pl.polsl.skirentalservice.dto.deliv_return.RentReturnEquipmentRecordResDto;
import pl.polsl.skirentalservice.entity.EquipmentEntity;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class EmailEquipmentPayloadDataDto {
    private String count;
    private String name;
    private String typeAndModel;
    private BigDecimal priceNetto;
    private BigDecimal priceBrutto;
    private BigDecimal depositPriceNetto;
    private BigDecimal depositPriceBrutto;

    public EmailEquipmentPayloadDataDto(EquipmentEntity eqEntity, RentReturnEquipmentRecordResDto eqDto) {
        name = eqEntity.getName();
        count = eqDto.count().toString();
        typeAndModel = eqEntity.getModel() + ", " + eqEntity.getEquipmentType().getName();
        depositPriceNetto = eqDto.depositPriceNetto();
    }

    @Override
    public String toString() {
        return "{" +
            "count='" + count +
            ", name='" + name +
            ", typeAndModel='" + typeAndModel +
            ", priceNetto=" + priceNetto +
            ", priceBrutto=" + priceBrutto +
            ", depositPriceNetto=" + depositPriceNetto +
            ", depositPriceBrutto=" + depositPriceBrutto +
            '}';
    }
}
