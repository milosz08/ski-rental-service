/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 * Silesian University of Technology
 *
 *  File name: EquipmentRecordResDto.java
 *  Last modified: 29/01/2023, 20:23
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.dto.equipment;

import java.math.BigDecimal;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public record EquipmentRecordResDto(
    Long id,
    String name,
    String type,
    String barcode,
    Integer countInStore,
    BigDecimal pricePerHour,
    BigDecimal priceForNextHour,
    BigDecimal pricePerDay,
    BigDecimal valueCost
) {
}
