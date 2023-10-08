/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.converter;

import jakarta.persistence.AttributeConverter;
import pl.polsl.skirentalservice.util.Gender;

import java.util.Objects;

public class GenderConverter implements AttributeConverter<Gender, String> {

    @Override
    public String convertToDatabaseColumn(Gender gender) {
        return gender.getName();
    }

    @Override
    public Gender convertToEntityAttribute(String name) {
        if (Objects.isNull(name)) return Gender.MALE;
        return switch (name) {
            case "kobieta" -> Gender.FEMALE;
            case "mężczyzna" -> Gender.MALE;
            default -> Gender.UNISEX;
        };
    }
}
