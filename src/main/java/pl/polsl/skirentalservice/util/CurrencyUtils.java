/*
 * Copyright (c) 2024 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CurrencyUtils {
    public static BigDecimal addTax(String taxValueAsStr, BigDecimal nettoPrice) {
        // add tax value with rouded to 2 pos after dot
        final BigDecimal taxValue = new BigDecimal(taxValueAsStr);
        return taxValue
            .divide(new BigDecimal(100), 2, RoundingMode.HALF_UP).multiply(nettoPrice).add(nettoPrice)
            .setScale(2, RoundingMode.HALF_UP);
    }

    public static BigDecimal addTax(int taxValueAsInt, BigDecimal nettoPrice) {
        return addTax(String.valueOf(taxValueAsInt), nettoPrice);
    }
}
