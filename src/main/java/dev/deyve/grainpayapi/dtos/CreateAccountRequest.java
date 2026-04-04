package dev.deyve.grainpayapi.dtos;

import dev.deyve.grainpayapi.models.AccountType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateAccountRequest(

        @NotBlank(message = "Name cannot be empty")
        String name,

        @NotNull(message = "Type cannot be null")
        AccountType type,

        String bankName,

        @NotNull(message = "Balance cannot be null")
        @DecimalMin(value = "0.00", message = "Balance must be zero or positive")
        BigDecimal balance
) {
}
