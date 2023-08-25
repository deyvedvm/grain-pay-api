package dev.deyve.grainpayapi.dummies;

import dev.deyve.grainpayapi.models.Income;

import java.math.BigDecimal;
import java.time.LocalDate;

public class IncomeDummy {

    public static Income.IncomeBuilder buildIncome() {

        return Income.builder()
                .id(1L)
                .description("Salary")
                .amount(BigDecimal.valueOf(1000.00))
                .date(LocalDate.of(2023, 4, 1));
    }
}
