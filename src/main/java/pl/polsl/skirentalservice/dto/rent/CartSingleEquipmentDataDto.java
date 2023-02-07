/*
 * Copyright (c) 2023 by multiple authors
 * Silesian University of Technology
 *
 *  File name: CartSingleEquipmentDataDto.java
 *  Last modified: 29/01/2023, 00:30
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.dto.rent;

import lombok.Data;
import java.math.BigDecimal;

import pl.polsl.skirentalservice.dto.PriceUnitsDto;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Data
public class CartSingleEquipmentDataDto {
    private Long id;
    private String name;
    private String typeAndModel;
    private String count;
    private String totalPersistCount;
    private String description;
    private PriceUnitsDto priceUnits;
    private AddEditEquipmentCartResDto resDto;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public CartSingleEquipmentDataDto(
        EquipmentRentRecordResDto equipmentDetails, AddEditEquipmentCartReqDto reqDto, AddEditEquipmentCartResDto resDto
    ) {
        id =  equipmentDetails.getId();
        typeAndModel = equipmentDetails.getType() + ", " + equipmentDetails.getModel();
        description = reqDto.getDescription();
        count = reqDto.getCount();
        priceUnits = new PriceUnitsDto();
        if (!reqDto.getDepositPrice().isEmpty()) {
            priceUnits.setTotalDepositPriceNetto(new BigDecimal(reqDto.getDepositPrice()));
        }
        this.resDto = resDto;
        totalPersistCount = equipmentDetails.getTotalCount().toString();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void setPrices(BigDecimal sumNetto, BigDecimal sumBrutto, BigDecimal depositBrutto) {
        priceUnits.setTotalPriceNetto(sumNetto);
        priceUnits.setTotalPriceBrutto(sumBrutto);
        priceUnits.setTotalDepositPriceBrutto(depositBrutto);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String toString() {
        return "{" +
            "id=" + id +
            ", name='" + name +
            ", typeAndModel='" + typeAndModel +
            ", count='" + count +
            ", totalPersistCount='" + totalPersistCount +
            ", description='" + description +
            ", priceUnits=" + priceUnits +
            ", resDto=" + resDto +
            '}';
    }
}
