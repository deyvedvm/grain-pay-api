package dev.deyve.grainpayapi.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import dev.deyve.grainpayapi.models.PaymentType;
import dev.deyve.grainpayapi.models.RecurrenceType;
import dev.deyve.grainpayapi.models.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record RecurringTransactionResponse(
        Long id,
        String description,
        BigDecimal amount,
        TransactionType type,
        PaymentType paymentType,
        CategoryResponse category,
        AccountResponse account,
        RecurrenceType recurrenceType,
        LocalDate startDate,
        LocalDate endDate,
        Integer dayOfMonth,
        Boolean isActive
) {
}
