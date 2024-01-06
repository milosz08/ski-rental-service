/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.pdf.dto;

import lombok.Data;
import pl.polsl.skirentalservice.dto.PriceUnitsDto;

import java.util.ArrayList;
import java.util.List;

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

    @Override
    public String toString() {
        return "{" +
            "issuedIdentifier=" + issuedIdentifier +
            ", fullName=" + fullName +
            ", pesel=" + pesel +
            ", phoneNumber=" + phoneNumber +
            ", email=" + email +
            ", address=" + address +
            ", rentDate=" + rentDate +
            ", returnDate=" + returnDate +
            ", rentTime=" + rentTime +
            ", tax=" + tax +
            ", totalSumPriceNetto=" + totalSumPriceNetto +
            ", totalSumPriceBrutto=" + totalSumPriceBrutto +
            ", description=" + description +
            ", priceUnits=" + priceUnits +
            ", equipments=" + equipments +
            '}';
    }
}
