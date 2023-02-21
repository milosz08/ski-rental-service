/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 * Silesian University of Technology
 *
 *  File name: EquipmentDetailsResDto.java
 *  Last modified: 06/02/2023, 19:25
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.dto.equipment;

import java.math.BigDecimal;

import pl.polsl.skirentalservice.util.Gender;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public record EquipmentDetailsResDto(
    Long id,
    String name,
    String type,
    String model,
    Gender gender,
    String barcode,
    String size,
    String brand,
    String color,
    String countData,
    Integer rentCount,
    BigDecimal pricePerHour,
    BigDecimal priceForNextHour,
    BigDecimal pricePerDay,
    BigDecimal valueCost,
    String description
) {
}
