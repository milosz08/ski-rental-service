/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.dto.rent;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

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

    public void setDisabled(boolean isDisabled) {
        this.disabled = isDisabled ? "disabled" : "";
    }

    @Override
    public String toString() {
        return "{" +
            "id=" + id +
            ", name=" + name +
            ", type=" + type +
            ", model=" + model +
            ", barcode=" + barcode +
            ", totalCount=" + totalCount +
            ", pricePerHour=" + pricePerHour +
            ", priceForNextHour=" + priceForNextHour +
            ", pricePerDay=" + pricePerDay +
            ", disabled=" + disabled +
            '}';
    }
}
