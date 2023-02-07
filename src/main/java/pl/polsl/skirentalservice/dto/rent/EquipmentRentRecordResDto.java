/*
 * Copyright (c) 2023 by multiple authors
 * Silesian University of Technology
 *
 *  File name: EquipmentRentRecordResDto.java
 *  Last modified: 28/01/2023, 20:00
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.dto.rent;

import lombok.*;
import java.math.BigDecimal;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Data
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
