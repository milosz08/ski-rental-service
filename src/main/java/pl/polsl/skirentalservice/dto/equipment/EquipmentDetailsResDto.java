package pl.polsl.skirentalservice.dto.equipment;

import pl.polsl.skirentalservice.util.Gender;

import java.math.BigDecimal;

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
