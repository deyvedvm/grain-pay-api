package dev.deyve.grainpayapi.dtos;

import dev.deyve.grainpayapi.models.IncomeSource;

import java.math.BigDecimal;

public record SourceSummary(IncomeSource source, BigDecimal total) {
}
