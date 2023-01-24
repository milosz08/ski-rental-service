/*
 * Copyright (c) 2023 by multiple authors
 * Silesian University of Technology
 *
 *  File name: NavcanvasActive.java
 *  Last modified: 24/01/2023, 21:49
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
