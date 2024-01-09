/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.dto.attribute;

import lombok.Data;
import lombok.NoArgsConstructor;
import pl.polsl.skirentalservice.core.ValidatorBean;
import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.dto.FormValueInfoTupleDto;

@Data
@NoArgsConstructor
public class AttributeModalResDto {
    private FormValueInfoTupleDto name;
    private AlertTupleDto alert;
    private String immediatelyShow;
    private NavCanvasState activeFirstPage = new NavCanvasState(true);
    private NavCanvasState activeSecondPage = new NavCanvasState(false);

    public AttributeModalResDto(ValidatorBean validator, AttributeModalReqDto reqDto, AlertTupleDto alert) {
        this.name = validator.validateField(reqDto, "name", reqDto.getName());
        this.alert = alert;
    }

    public void setModalImmediatelyOpen(boolean isImmediatelyOpen) {
        this.immediatelyShow = isImmediatelyOpen ? "open" : "close";
    }

    @Override
    public String toString() {
        return "{" +
            "name=" + name +
            ", alert=" + alert +
            '}';
    }
}
