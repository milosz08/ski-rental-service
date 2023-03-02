/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 * Silesian University of Technology
 *
 *  File name: RentReturnEmailPayloadDataDto.java
 *  Last modified: 07/02/2023, 16:04
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.HashSet;
import java.math.BigDecimal;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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
