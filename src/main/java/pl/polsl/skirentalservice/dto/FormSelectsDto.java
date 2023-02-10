/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 * Silesian University of Technology
 *
 *  File name: FormSelectsDto.java
 *  Last modified: 24/01/2023, 17:43
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.dto;

import lombok.*;
import java.util.*;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Data
public class FormSelectsDto {
    private List<FormSelectTupleDto> selects;
    private String selected;
    private String errorStyle;
    private String message;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public FormSelectsDto() {
        this.selects = new ArrayList<>();
        this.selected = "none";
        this.errorStyle = "";
        this.message = "";
    }
}
