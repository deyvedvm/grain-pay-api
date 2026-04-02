package dev.deyve.grainpayapi.dtos;

import dev.deyve.grainpayapi.models.IncomeSource;
import dev.deyve.grainpayapi.models.PaymentType;
import dev.deyve.grainpayapi.models.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

public record CreateTransactionRequest(

        @NotNull(message = "Type cannot be null")
        TransactionType type,

        @NotNull(message = "Amount cannot be null")
        @Positive(message = "Amount must be positive")
        BigDecimal amount,

        @NotNull(message = "Date cannot be null")
        LocalDate date,

        @NotBlank(message = "Description cannot be empty")
        String description,

        PaymentType paymentType,

        Long categoryId,

        String notes,

        Set<String> tags,

        // EXPENSE-specific
        Integer installments,

        Integer currentInstallment,

        Boolean isRecurring,

        // INCOME-specific
        IncomeSource source
) {
}
