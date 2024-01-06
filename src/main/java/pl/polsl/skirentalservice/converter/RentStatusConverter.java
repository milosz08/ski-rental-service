/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.converter;

import jakarta.persistence.AttributeConverter;
import pl.polsl.skirentalservice.util.RentStatus;

public class RentStatusConverter implements AttributeConverter<RentStatus, String> {
    @Override
    public String convertToDatabaseColumn(RentStatus rentStatus) {
        return rentStatus.getStatus();
    }

    @Override
    public RentStatus convertToEntityAttribute(String name) {
        return name == null ? RentStatus.OPENED : switch (name) {
            case "wypożyczony" -> RentStatus.RENTED;
            case "zwrócony" -> RentStatus.RETURNED;
            default -> RentStatus.OPENED;
        };
    }
}
