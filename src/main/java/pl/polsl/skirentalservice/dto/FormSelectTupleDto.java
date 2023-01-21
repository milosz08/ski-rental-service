/*
 * Copyright (c) 2023 by multiple authors
 * Silesian University of Technology
 *
 *  File name: FormSelectTupleDto.java
 *  Last modified: 21/01/2023, 16:18
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.dto;

import lombok.*;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Data
public class FormSelectTupleDto {
    private String value;
    private String text;
    private String isSelected;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public FormSelectTupleDto(boolean isSelected, String value, String text) {
        this.value = value;
        this.text = text;
        this.isSelected = isSelected ? "selected" : "";
    }
}
