package pl.polsl.skirentalservice.dto.rent;

import lombok.Data;
import pl.polsl.skirentalservice.dto.PriceUnitsDto;
import pl.polsl.skirentalservice.dto.customer.CustomerDetailsResDto;
import pl.polsl.skirentalservice.util.DateUtils;
import pl.polsl.skirentalservice.util.RentStatus;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Data
public class InMemoryRentDataDto {
    private Long customerId;
    private String customerFullName;
    private String issuedIdentifier;
    private String issuedDateTime;
    private String rentDateTime;
    private String returnDateTime;
    private RentStatus rentStatus;
    private String description;
    private String tax;
    private PriceUnitsDto priceUnits;
    private long days;
    private long hours;
    private int totalCount;
    private boolean allGood;
    private CustomerDetailsResDto customerDetails;
    private List<CartSingleEquipmentDataDto> equipments = new ArrayList<>();

    public InMemoryRentDataDto(Long customerId, String customerFullName) {
        this.customerId = customerId;
        this.customerFullName = customerFullName;
        this.issuedDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES).toString().replace('T', ' ');
        this.rentStatus = RentStatus.OPENED;
        this.priceUnits = new PriceUnitsDto();
    }

    public LocalDateTime getParsedRentDateTime() {
        return DateUtils.parse(rentDateTime);
    }

    public LocalDateTime getParsedReturnDateTime() {
        return DateUtils.parse(returnDateTime);
    }

    @Override
    public String toString() {
        return "{" +
            "customerId=" + customerId +
            ", customerFullName=" + customerFullName +
            ", issuedIdentifier=" + issuedIdentifier +
            ", issuedDateTime=" + issuedDateTime +
            ", rentDateTime=" + rentDateTime +
            ", returnDateTime=" + returnDateTime +
            ", rentStatus=" + rentStatus +
            ", description=" + description +
            ", tax=" + tax +
            ", priceUnits=" + priceUnits +
            ", days=" + days +
            ", hours=" + hours +
            ", totalCount=" + totalCount +
            ", allGood=" + allGood +
            ", equipments=" + equipments +
            '}';
    }
}
