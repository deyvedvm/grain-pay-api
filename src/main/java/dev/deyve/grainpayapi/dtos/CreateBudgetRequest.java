package dev.deyve.grainpayapi.dtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateBudgetRequest(

        @NotNull(message = "Category cannot be null")
        Long categoryId,

        @NotNull(message = "Limit amount cannot be null")
        @DecimalMin(value = "0.01", message = "Limit amount must be greater than zero")
        BigDecimal limitAmount,

        @NotNull(message = "Month cannot be null")
        @Min(value = 1, message = "Month must be between 1 and 12")
        @Max(value = 12, message = "Month must be between 1 and 12")
        Integer month,

        @NotNull(message = "Year cannot be null")
        @Min(value = 2000, message = "Year must be 2000 or later")
        Integer year
) {
}
