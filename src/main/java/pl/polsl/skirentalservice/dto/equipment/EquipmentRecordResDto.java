package pl.polsl.skirentalservice.dto.equipment;

import java.math.BigDecimal;

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
