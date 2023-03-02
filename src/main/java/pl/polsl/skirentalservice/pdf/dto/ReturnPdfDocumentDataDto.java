/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 * Silesian University of Technology
 *
 *  File name: ReturnPdfDocumentDataDto.java
 *  Last modified: 09/02/2023, 01:22
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.pdf.dto;

import lombok.Data;

import java.util.List;
import java.util.ArrayList;

import pl.polsl.skirentalservice.dto.PriceUnitsDto;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Data
public class ReturnPdfDocumentDataDto {
    private String issuedIdentifier;
    private String fullName;
    private String pesel;
    private String phoneNumber;
    private String email;
    private String address;
    private String rentDate;
    private String returnDate;
    private String rentTime;
    private String tax;
    private String totalSumPriceNetto;
    private String totalSumPriceBrutto;
    private String description;
    private PriceUnitsDto priceUnits = new PriceUnitsDto();
    private List<PdfEquipmentDataDto> equipments = new ArrayList<>();

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String toString() {
        return "{" +
            "issuedIdentifier='" + issuedIdentifier +
            ", fullName='" + fullName +
            ", pesel='" + pesel +
            ", phoneNumber='" + phoneNumber +
            ", email='" + email +
            ", address='" + address +
            ", rentDate='" + rentDate +
            ", returnDate='" + returnDate +
            ", rentTime='" + rentTime +
            ", tax='" + tax +
            ", totalSumPriceNetto='" + totalSumPriceNetto +
            ", totalSumPriceBrutto='" + totalSumPriceBrutto +
            ", description='" + description +
            ", priceUnits=" + priceUnits +
            ", equipments=" + equipments +
            '}';
    }
}
