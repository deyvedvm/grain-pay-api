package dev.deyve.grainpayapi.dummies;

import dev.deyve.grainpayapi.models.Expense;
import dev.deyve.grainpayapi.models.PaymentType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ExpenseDummy {

    public static Expense.ExpenseBuilder buildExpense() {
        return Expense.builder()
                .id(1L)
                .description("Expense description")
                .amount(BigDecimal.valueOf(150.00))
                .date(LocalDateTime.of(2023, 4, 2, 0, 0))
                .paymentType(PaymentType.CREDIT_CARD);
    }
}
