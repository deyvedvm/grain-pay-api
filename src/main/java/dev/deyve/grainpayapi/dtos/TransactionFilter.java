package dev.deyve.grainpayapi.dtos;

import dev.deyve.grainpayapi.models.PaymentType;
import dev.deyve.grainpayapi.models.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionFilter(
        TransactionType type,
        LocalDate startDate,
        LocalDate endDate,
        Long categoryId,
        PaymentType paymentType,
        BigDecimal minAmount,
        BigDecimal maxAmount
) {
}
