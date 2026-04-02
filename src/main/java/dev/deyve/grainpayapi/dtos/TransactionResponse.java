package dev.deyve.grainpayapi.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import dev.deyve.grainpayapi.models.IncomeSource;
import dev.deyve.grainpayapi.models.PaymentType;
import dev.deyve.grainpayapi.models.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record TransactionResponse(
        Long id,
        TransactionType type,
        BigDecimal amount,
        LocalDate date,
        String description,
        PaymentType paymentType,
        CategoryResponse category,
        String notes,
        Set<String> tags,
        Long userId,
        Integer installments,
        Integer currentInstallment,
        Boolean isRecurring,
        IncomeSource source,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
