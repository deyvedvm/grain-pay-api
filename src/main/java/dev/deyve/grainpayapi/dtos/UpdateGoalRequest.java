package dev.deyve.grainpayapi.dtos;

import dev.deyve.grainpayapi.models.GoalPriority;
import dev.deyve.grainpayapi.models.GoalStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdateGoalRequest(

        @NotBlank(message = "Name cannot be empty")
        String name,

        @NotNull(message = "Target amount cannot be null")
        @DecimalMin(value = "0.01", message = "Target amount must be greater than zero")
        BigDecimal targetAmount,

        @NotNull(message = "Current amount cannot be null")
        @PositiveOrZero(message = "Current amount must be zero or positive")
        BigDecimal currentAmount,

        @NotNull(message = "Deadline cannot be null")
        LocalDate deadline,

        String description,

        @NotNull(message = "Priority cannot be null")
        GoalPriority priority,

        @NotNull(message = "Status cannot be null")
        GoalStatus status

) {
}
