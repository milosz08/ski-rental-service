/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.dto;

import lombok.Data;

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
        this.errorStyle = "";
        this.message = "";
    }
}
