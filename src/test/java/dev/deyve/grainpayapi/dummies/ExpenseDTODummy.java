package dev.deyve.grainpayapi.dummies;

import dev.deyve.grainpayapi.dtos.ExpenseDTO;
import dev.deyve.grainpayapi.models.PaymentType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ExpenseDTODummy {

    public static ExpenseDTO buildExpenseDTO() {
        return new ExpenseDTO(
                1L,
                "Mock description",
                BigDecimal.TEN,
                LocalDateTime.of(2023, 4, 1, 0, 0),
                PaymentType.MONEY,
                LocalDateTime.of(2023, 4, 1, 0, 0),
                LocalDateTime.of(2023, 4, 1, 0, 0)
        );
    }
}
