/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.dto.attribute;

import lombok.Data;

@Data
public class NavCanvasState {
    private String button;
    private String canvas;

    NavCanvasState(boolean isActive) {
        this.button = isActive ? "active" : "";
        this.canvas = isActive ? "show active" : "";
    }

    public void setActive(boolean isActive) {
        this.button = isActive ? "active" : "";
        this.canvas = isActive ? "show active" : "";
    }
}
