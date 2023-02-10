/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 * Silesian University of Technology
 *
 *  File name: NavCanvasState.java
 *  Last modified: 24/01/2023, 22:08
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.dto.attribute;

import lombok.Data;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Data
public class NavCanvasState {
    private String button;
    private String canvas;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    NavCanvasState(boolean isActive) {
        this.button = isActive ? "active" : "";
        this.canvas = isActive ? "show active" : "";
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void setActive(boolean isActive) {
        this.button = isActive ? "active" : "";
        this.canvas = isActive ? "show active" : "";
    }
}
