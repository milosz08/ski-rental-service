package pl.polsl.skirentalservice.dto;

import pl.polsl.skirentalservice.dto.customer.CustomerDetailsResDto;
import pl.polsl.skirentalservice.dto.employer.EmployerDetailsResDto;

public record UpdatedInMemoryRentData(
    boolean hasEmptyEquipments,
    CustomerDetailsResDto customerDetails,
    EmployerDetailsResDto employerDetails
) {
    public UpdatedInMemoryRentData(boolean hasEmptyEquipments) {
        this(hasEmptyEquipments, null, null);
    }
}
