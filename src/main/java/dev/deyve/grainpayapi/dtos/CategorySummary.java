package dev.deyve.grainpayapi.dtos;

import java.math.BigDecimal;

public record CategorySummary(String categoryName, BigDecimal total) {
}
