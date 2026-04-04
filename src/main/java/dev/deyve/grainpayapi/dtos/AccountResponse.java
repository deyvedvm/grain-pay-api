package dev.deyve.grainpayapi.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import dev.deyve.grainpayapi.models.AccountType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record AccountResponse(
        Long id,
        String name,
        AccountType type,
        String bankName,
        BigDecimal balance,
        LocalDateTime createdAt
) {
}
