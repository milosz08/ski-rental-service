package pl.polsl.skirentalservice.dto.rent;

import lombok.Data;
import lombok.NoArgsConstructor;
import pl.polsl.skirentalservice.core.ValidatorBean;
import pl.polsl.skirentalservice.dto.FormValueInfoTupleDto;
import pl.polsl.skirentalservice.util.DateUtils;
import pl.polsl.skirentalservice.util.RentStatus;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class NewRentDetailsResDto {
    private String issuedIdentifier;
    private String issuedDateTime;
    private RentStatus rentStatus;
    private FormValueInfoTupleDto rentDateTime = new FormValueInfoTupleDto();
    private FormValueInfoTupleDto returnDateTime = new FormValueInfoTupleDto();
    private FormValueInfoTupleDto tax = new FormValueInfoTupleDto();
    private FormValueInfoTupleDto description = new FormValueInfoTupleDto();

    public NewRentDetailsResDto(ValidatorBean validator, NewRentDetailsReqDto reqDto) {
        this.rentDateTime = validator.validateField(reqDto, "rentDateTime", reqDto.getRentDateTime());
        this.returnDateTime = validator.validateField(reqDto, "returnDateTime", reqDto.getReturnDateTime());
        this.tax = validator.validateField(reqDto, "tax", reqDto.getTax());
        this.description = validator.validateField(reqDto, "description", reqDto.getDescription());
    }

    public LocalDateTime getParsedIssuedDateTime() {
        return DateUtils.parse(issuedDateTime);
    }

    @Override
    public String toString() {
        return "{" +
            "issuedIdentifier=" + issuedIdentifier +
            ", issuedDateTime=" + issuedDateTime +
            ", rentStatus=" + rentStatus +
            ", rentDateTime=" + rentDateTime +
            ", returnDateTime=" + returnDateTime +
            ", tax=" + tax +
            ", description=" + description +
            '}';
    }
}
