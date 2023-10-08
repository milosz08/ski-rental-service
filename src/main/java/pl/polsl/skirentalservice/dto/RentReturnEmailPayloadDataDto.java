/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
public class RentReturnEmailPayloadDataDto {
    private String fullName;
    private String pesel;
    private String phoneNumber;
    private String email;
    private String rentDate;
    private String returnDate;
    private String rentTime;
    private String tax;
    private BigDecimal totalPriceBrutto;
    private BigDecimal totalPriceNetto;
    private BigDecimal totalDepositPriceBrutto;
    private BigDecimal totalPriceWithDepositBrutto;
    private Set<EmailEquipmentPayloadDataDto> rentEquipments = new HashSet<>();

    @Override
    public String toString() {
        return "{" +
            "fullName='" + fullName +
            ", pesel='" + pesel +
            ", phoneNumber='" + phoneNumber +
            ", email='" + email +
            ", rentDate='" + rentDate +
            ", returnDate='" + returnDate +
            ", rentTime='" + rentTime +
            ", tax='" + tax +
            ", totalPriceBrutto=" + totalPriceBrutto +
            ", totalPriceNetto=" + totalPriceNetto +
            ", totalDepositPriceBrutto=" + totalDepositPriceBrutto +
            ", totalPriceWithDepositBrutto=" + totalPriceWithDepositBrutto +
            ", rentEquipments=" + rentEquipments +
            '}';
    }
}
