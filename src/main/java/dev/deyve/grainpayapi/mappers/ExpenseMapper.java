package dev.deyve.grainpayapi.mappers;

import dev.deyve.grainpayapi.dtos.ExpenseDTO;
import dev.deyve.grainpayapi.models.Expense;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ExpenseMapper {

    ExpenseDTO toDTO(Expense expense);

    Expense toEntity(ExpenseDTO expenseDTO);
}
