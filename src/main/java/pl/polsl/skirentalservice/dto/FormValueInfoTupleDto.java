/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
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
