package dev.deyve.grainpayapi.dtos;

import java.math.BigDecimal;

public record MonthlyReportItem(
        Integer month,
        BigDecimal totalIncome,
        BigDecimal totalExpenses,
        BigDecimal balance
) {
}
