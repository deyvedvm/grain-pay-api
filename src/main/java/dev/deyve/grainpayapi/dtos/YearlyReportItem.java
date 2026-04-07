package dev.deyve.grainpayapi.dtos;

import java.math.BigDecimal;

public record YearlyReportItem(
        Integer year,
        BigDecimal totalIncome,
        BigDecimal totalExpenses,
        BigDecimal balance
) {
}
