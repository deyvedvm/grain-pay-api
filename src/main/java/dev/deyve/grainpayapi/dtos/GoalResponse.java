package dev.deyve.grainpayapi.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import dev.deyve.grainpayapi.models.GoalPriority;
import dev.deyve.grainpayapi.models.GoalStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record GoalResponse(
        Long id,
        String name,
        BigDecimal targetAmount,
        BigDecimal currentAmount,
        BigDecimal progress,
        LocalDate deadline,
        String description,
        GoalPriority priority,
        GoalStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
