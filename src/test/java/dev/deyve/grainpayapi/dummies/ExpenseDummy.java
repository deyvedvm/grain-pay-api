package dev.deyve.grainpayapi.dummies;

import dev.deyve.grainpayapi.models.Expense;
import dev.deyve.grainpayapi.models.PaymentType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ExpenseDummy {

    public static Expense buildExpense() {
        return new Expense(
                1L,
                "Expense description",
                BigDecimal.valueOf(150.00),
                LocalDateTime.of(2023, 4, 2, 0, 0),
                PaymentType.CREDIT_CARD
        );
    }
}
