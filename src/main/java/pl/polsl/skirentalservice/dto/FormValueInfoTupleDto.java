package pl.polsl.skirentalservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FormValueInfoTupleDto {
    private String value;
    private String errorStyle;
    private String message;

    public FormValueInfoTupleDto(String value) {
        this.value = value;
    }
}
