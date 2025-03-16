package pl.polsl.skirentalservice.dto.rent;

import java.math.BigDecimal;

public record RentEquipmentsDetailsResDto(
    Long id,
    String name,
    Integer count,
    String barcode,
    String description,
    BigDecimal priceNetto,
    BigDecimal priceBrutto,
    BigDecimal depositPriceNetto,
    BigDecimal depositPriceBrutto
) {
}
