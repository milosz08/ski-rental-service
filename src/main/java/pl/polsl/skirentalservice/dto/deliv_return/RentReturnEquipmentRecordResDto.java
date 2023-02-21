/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 * Silesian University of Technology
 *
 *  File name: RentReturnEquipmentRecordResDto.java
 *  Last modified: 31/01/2023, 01:53
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.dto.deliv_return;

import java.math.BigDecimal;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public record RentReturnEquipmentRecordResDto(
    Long id,
    BigDecimal pricePerHour,
    BigDecimal priceForNextHour,
    BigDecimal pricePerDay,
    Integer count,
    BigDecimal depositPriceNetto,
    Long equipmentId,
    String description
) {
}
