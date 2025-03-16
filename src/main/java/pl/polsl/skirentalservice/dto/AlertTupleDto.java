package pl.polsl.skirentalservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import pl.polsl.skirentalservice.util.AlertType;

@Data
@AllArgsConstructor
public class AlertTupleDto {
    private boolean active;
    private String message;
    private AlertType type;
    private boolean disableContent;

    public AlertTupleDto(boolean active) {
        this();
        this.active = active;
    }

    public AlertTupleDto() {
        this.type = AlertType.ERROR;
    }
}
