package dev.deyve.grainpayapi.dummies;

import dev.deyve.grainpayapi.dtos.IncomeDTO;

import java.math.BigDecimal;
import java.time.LocalDate;

public class IncomeDTODummy {

    public static IncomeDTO.IncomeDTOBuilder buildIncomeDTO() {

        return IncomeDTO.builder()
                .id(1L)
                .description("Salary")
                .amount(BigDecimal.valueOf(1000.00))
                .date(LocalDate.of(2023, 4, 1))
                .createdAt(LocalDate.of(2023, 4, 1).atStartOfDay())
                .updatedAt(LocalDate.of(2023, 4, 1).atStartOfDay());

    }

}
