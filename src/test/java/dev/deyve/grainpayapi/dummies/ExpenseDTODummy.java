package dev.deyve.grainpayapi.dummies;

import dev.deyve.grainpayapi.dtos.ExpenseDTO;
import dev.deyve.grainpayapi.models.PaymentType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ExpenseDTODummy {

    public static ExpenseDTO.ExpenseDTOBuilder buildExpenseDTO() {
        return ExpenseDTO.builder()
                .id(1L)
                .description("Mock description")
                .amount(BigDecimal.TEN)
                .date(LocalDateTime.of(2023, 4, 1, 0, 0))
                .paymentType(PaymentType.MONEY)
                .createdAt(LocalDateTime.of(2023, 4, 1, 0, 0))
                .updatedAt(LocalDateTime.of(2023, 4, 1, 0, 0));
    }
}
