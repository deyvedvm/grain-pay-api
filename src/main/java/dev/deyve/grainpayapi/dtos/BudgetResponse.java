package dev.deyve.grainpayapi.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record BudgetResponse(
        Long id,
        CategoryResponse category,
        BigDecimal limitAmount,
        BigDecimal spent,
        BigDecimal percentage,
        Boolean alert,
        Integer month,
        Integer year,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
