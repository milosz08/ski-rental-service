/*
 * Copyright (c) 2023 by multiple authors
 * Silesian University of Technology
 *
 *  File name: RentPdfDocumentDataDto.java
 *  Last modified: 08/02/2023, 21:21
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.pdf.dto;

import lombok.Data;
import java.util.*;

import pl.polsl.skirentalservice.dto.PriceUnitsDto;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Data
public class RentPdfDocumentDataDto {
    private String issuedIdentifier;
    private String fullName;
    private String pesel;
    private String phoneNumber;
    private String email;
    private String address;
    private String issuedDate;
    private String rentDate;
    private String returnDate;
    private String rentTime;
    private String tax;
    private PriceUnitsDto priceUnits;
    private String totalSumPriceNetto;
    private String totalSumPriceBrutto;
    private String description;
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
            ", issuedDate='" + issuedDate +
            ", rentDate='" + rentDate +
            ", returnDate='" + returnDate +
            ", rentTime='" + rentTime +
            ", tax='" + tax +
            ", priceUnits=" + priceUnits +
            ", totalSumPriceNetto='" + totalSumPriceNetto +
            ", totalSumPriceBrutto='" + totalSumPriceBrutto +
            ", description='" + description +
            ", equipments=" + equipments +
            '}';
    }
}
