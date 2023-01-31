/*
 * Copyright (c) 2023 by multiple authors
 * Silesian University of Technology
 *
 *  File name: RentConverted.java
 *  Last modified: 31/01/2023, 04:46
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.converter;

import jakarta.persistence.AttributeConverter;
import pl.polsl.skirentalservice.util.RentStatus;

import static java.util.Objects.isNull;
import static pl.polsl.skirentalservice.util.RentStatus.*;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public class RentStatusConverter implements AttributeConverter<RentStatus, String> {

    @Override
    public String convertToDatabaseColumn(RentStatus rentStatus) {
        return rentStatus.getStatus();
    }

    @Override
    public RentStatus convertToEntityAttribute(String name) {
        if (isNull(name)) return OPENED;
        switch (name) {
            case "otwarty": return OPENED;
            case "wypożyczony": return RENTED;
            case "zwrócony": return RETURNED;
        }
        return OPENED;
    }
}
