/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 * Silesian University of Technology
 *
 *  File name: GenderConverter.java
 *  Last modified: 31/01/2023, 05:32
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.converter;

import jakarta.persistence.AttributeConverter;
import pl.polsl.skirentalservice.util.Gender;

import static java.util.Objects.isNull;
import static pl.polsl.skirentalservice.util.Gender.*;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public class GenderConverter implements AttributeConverter<Gender, String> {

    @Override
    public String convertToDatabaseColumn(Gender gender) {
        return gender.getName();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Gender convertToEntityAttribute(String name) {
        if (isNull(name)) return MALE;
        switch (name) {
            case "kobieta": return FEMALE;
            case "mężczyzna": return MALE;
            case "unisex": return UNISEX;
        }
        return UNISEX;
    }
}
