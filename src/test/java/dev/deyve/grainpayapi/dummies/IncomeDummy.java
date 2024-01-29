package dev.deyve.grainpayapi.dummies;

import dev.deyve.grainpayapi.models.Income;

import java.math.BigDecimal;
import java.time.LocalDate;

public class IncomeDummy {

    public static Income buildIncome() {
        return new Income(
                1L,
                "Salary",
                BigDecimal.valueOf(1000.00),
                LocalDate.of(2023, 4, 1)
        );
    }

    public static Income buildIncome(String description) {
        return new Income(
                1L,
                description,
                BigDecimal.valueOf(1000.00),
                LocalDate.of(2023, 4, 1)
        );
    }

    public static Income buildIncome(BigDecimal amount) {
        return new Income(
                1L,
                "Salary",
                amount,
                LocalDate.of(2023, 4, 1)
        );
    }
}
