package pl.polsl.skirentalservice.dto.rent;

import lombok.Data;
import lombok.NoArgsConstructor;
import pl.polsl.skirentalservice.core.ValidatorBean;
import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.dto.FormValueInfoTupleDto;

@Data
@NoArgsConstructor
public class AddEditEquipmentCartResDto {
    private FormValueInfoTupleDto count;
    private FormValueInfoTupleDto depositPrice;
    private FormValueInfoTupleDto description;
    private AlertTupleDto alert;
    private String immediatelyShow;
    private String eqId;

    public AddEditEquipmentCartResDto(ValidatorBean validator, AddEditEquipmentCartReqDto reqDto) {
        this.count = validator.validateField(reqDto, "count", reqDto.getCount());
        this.depositPrice = validator.validateField(reqDto, "depositPrice", reqDto.getDepositPrice());
        this.description = validator.validateField(reqDto, "description", reqDto.getDescription());
    }

    public void setModalImmediatelyOpen(boolean isImmediatelyOpen) {
        this.immediatelyShow = isImmediatelyOpen ? "open" : "close";
    }

    public void setEquipmentId(Long equipmentId) {
        eqId = String.valueOf(equipmentId);
    }

    @Override
    public String toString() {
        return "{" +
            "count=" + count +
            ", depositPrice=" + depositPrice +
            ", description=" + description +
            '}';
    }
}
