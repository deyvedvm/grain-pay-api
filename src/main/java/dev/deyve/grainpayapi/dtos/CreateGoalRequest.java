package dev.deyve.grainpayapi.dtos;

import dev.deyve.grainpayapi.models.GoalPriority;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateGoalRequest(

        @NotBlank(message = "Name cannot be empty")
        String name,

        @NotNull(message = "Target amount cannot be null")
        @DecimalMin(value = "0.01", message = "Target amount must be greater than zero")
        BigDecimal targetAmount,

        @NotNull(message = "Deadline cannot be null")
        @Future(message = "Deadline must be a future date")
        LocalDate deadline,

        String description,

        @NotNull(message = "Priority cannot be null")
        GoalPriority priority

) {
}
