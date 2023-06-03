/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 * Silesian University of Technology
 *
 *  File name: EquipmentRentRecordResDto.java
 *  Last modified: 07/02/2023, 13:11
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.dto.rent;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EquipmentRentRecordResDto {
    private Long id;
    private String name;
    private String type;
    private String model;
    private String barcode;
    private Integer totalCount;
    private BigDecimal pricePerHour;
    private BigDecimal priceForNextHour;
    private BigDecimal pricePerDay;
    private String disabled;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void setDisabled(boolean isDisabled) {
        this.disabled = isDisabled ? "disabled" : "";
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String toString() {
        return "{" +
            "id=" + id +
            ", name='" + name +
            ", type='" + type +
            ", model='" + model +
            ", barcode='" + barcode +
            ", totalCount=" + totalCount +
            ", pricePerHour=" + pricePerHour +
            ", priceForNextHour=" + priceForNextHour +
            ", pricePerDay=" + pricePerDay +
            ", disabled='" + disabled +
            '}';
    }
}
