package pl.polsl.skirentalservice.dto.attribute;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
public class NavCanvasState {
    private String button;
    private String canvas;

    NavCanvasState(boolean isActive) {
        this.button = isActive ? "active" : StringUtils.EMPTY;
        this.canvas = isActive ? "show active" : StringUtils.EMPTY;
    }

    public void setActive(boolean isActive) {
        this.button = isActive ? "active" : StringUtils.EMPTY;
        this.canvas = isActive ? "show active" : StringUtils.EMPTY;
    }
}
