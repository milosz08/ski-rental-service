package pl.polsl.skirentalservice.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PriceUnitsDto {
    private BigDecimal totalPriceNetto;
    private BigDecimal totalPriceBrutto;
    private BigDecimal totalDepositPriceNetto;
    private BigDecimal totalDepositPriceBrutto;

    public PriceUnitsDto() {
        this.totalPriceNetto = new BigDecimal("0.00");
        this.totalPriceBrutto = new BigDecimal("0.00");
        this.totalDepositPriceNetto = new BigDecimal("0.00");
        this.totalDepositPriceBrutto = new BigDecimal("0.00");
    }

    public void add(BigDecimal netto, BigDecimal brutto, BigDecimal depositNetto, BigDecimal depositBrutto) {
        totalPriceNetto = totalPriceNetto.add(netto);
        totalPriceBrutto = totalPriceBrutto.add(brutto);
        totalDepositPriceNetto = totalDepositPriceNetto.add(depositNetto);
        totalDepositPriceBrutto = totalDepositPriceBrutto.add(depositBrutto);
    }

    public String mergeWithDepositNettoPrice() {
        return totalPriceNetto.add(totalDepositPriceNetto).toString();
    }

    public String mergeWithDepositBruttoPrice() {
        return totalPriceBrutto.add(totalDepositPriceBrutto).toString();
    }

    @Override
    public String toString() {
        return "{" +
            "totalPriceNetto=" + totalPriceNetto +
            ", totalPriceBrutto=" + totalPriceBrutto +
            ", totalDepositPriceNetto=" + totalDepositPriceNetto +
            ", totalDepositPriceBrutto=" + totalDepositPriceBrutto +
            '}';
    }
}
