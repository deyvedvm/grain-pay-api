package dev.deyve.grainpayapi.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import dev.deyve.grainpayapi.models.TransactionType;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CategoryResponse(
        Long id,
        String name,
        TransactionType type,
        String icon,
        String color,
        LocalDateTime createdAt
) {
}
