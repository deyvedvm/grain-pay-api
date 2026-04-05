package dev.deyve.grainpayapi.dtos;

import dev.deyve.grainpayapi.models.PaymentType;
import dev.deyve.grainpayapi.models.RecurrenceType;
import dev.deyve.grainpayapi.models.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateRecurringTransactionRequest(

        @NotBlank(message = "Description cannot be empty")
        String description,

        @NotNull(message = "Amount cannot be null")
        @Positive(message = "Amount must be positive")
        BigDecimal amount,

        @NotNull(message = "Type cannot be null")
        TransactionType type,

        PaymentType paymentType,

        Long categoryId,

        Long accountId,

        @NotNull(message = "Recurrence type cannot be null")
        RecurrenceType recurrenceType,

        @NotNull(message = "Start date cannot be null")
        LocalDate startDate,

        LocalDate endDate,

        Integer dayOfMonth
) {
}
