package dev.deyve.grainpayapi.dtos;

import java.math.BigDecimal;

public record CategoryReportItem(
        String categoryName,
        BigDecimal totalIncome,
        BigDecimal totalExpenses
) {
}
