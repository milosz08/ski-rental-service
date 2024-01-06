/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.dto;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
public class FormSelectTupleDto {
    private String value;
    private String text;
    private String isSelected;

    public FormSelectTupleDto(boolean isSelected, String value, String text) {
        this.value = value;
        this.text = text;
        this.isSelected = isSelected ? "selected" : StringUtils.EMPTY;
    }

    public FormSelectTupleDto(String value, String text) {
        this.value = value;
        this.text = text;
    }
}
