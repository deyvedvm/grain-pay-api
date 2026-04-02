package dev.deyve.grainpayapi.dtos;

import dev.deyve.grainpayapi.models.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateCategoryRequest(

        @NotBlank(message = "Name cannot be empty")
        String name,

        @NotNull(message = "Type cannot be null")
        TransactionType type,

        String icon,

        String color
) {
}
