package dev.deyve.grainpayapi.dummies;

import dev.deyve.grainpayapi.dtos.IncomeDTO;

import java.math.BigDecimal;
import java.time.LocalDate;

public class IncomeDTODummy {

    public static IncomeDTO buildIncomeDTO() {
        return new IncomeDTO(
                1L,
                "Salary",
                BigDecimal.valueOf(1000.00),
                LocalDate.of(2023, 4, 1),
                LocalDate.of(2023, 4, 1).atStartOfDay(),
                LocalDate.of(2023, 4, 1).atStartOfDay()
        );
    }

    public static IncomeDTO buildIncomeDTO(String description) {
        return new IncomeDTO(
                1L,
                description,
                BigDecimal.valueOf(1000.00),
                LocalDate.of(2023, 4, 1),
                LocalDate.of(2023, 4, 1).atStartOfDay(),
                LocalDate.of(2023, 4, 1).atStartOfDay()
        );
    }

    public static IncomeDTO buildIncomeDTO(BigDecimal amount) {
        return new IncomeDTO(
                1L,
                "Salary",
                amount,
                LocalDate.of(2023, 4, 1),
                LocalDate.of(2023, 4, 1).atStartOfDay(),
                LocalDate.of(2023, 4, 1).atStartOfDay()
        );
    }

}
