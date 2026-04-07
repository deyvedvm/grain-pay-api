package dev.deyve.grainpayapi.dtos;

import java.math.BigDecimal;
import java.util.List;

public record DashboardSummaryResponse(
        BigDecimal totalIncome,
        BigDecimal totalExpenses,
        BigDecimal balance,
        List<CategorySummary> expensesByCategory,
        List<SourceSummary> incomeBySource
) {
}
