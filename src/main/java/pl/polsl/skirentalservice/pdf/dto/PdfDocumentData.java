package pl.polsl.skirentalservice.pdf.dto;

import lombok.Getter;
import lombok.Setter;
import pl.polsl.skirentalservice.dto.PriceUnitsDto;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PdfDocumentData {
    private String issuedIdentifier;
    private String fullName;
    private String pesel;
    private String phoneNumber;
    private String email;
    private String address;
    private String rentDate;
    private String issuedDate;
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
            ", issuedDate=" + issuedDate +
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
