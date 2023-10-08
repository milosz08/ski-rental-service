/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.dto.deliv_return;

import java.math.BigDecimal;

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
