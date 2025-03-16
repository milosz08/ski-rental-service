package pl.polsl.skirentalservice.dto;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Data
public class FormSelectsDto {
    private List<FormSelectTupleDto> selects;
    private String selected;
    private String errorStyle;
    private String message;

    public FormSelectsDto() {
        this.selects = new ArrayList<>();
        this.selected = "none";
        this.errorStyle = StringUtils.EMPTY;
        this.message = StringUtils.EMPTY;
    }
}
