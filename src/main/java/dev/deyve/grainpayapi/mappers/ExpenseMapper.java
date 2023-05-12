package dev.deyve.grainpayapi.mappers;

import dev.deyve.grainpayapi.dtos.ExpenseDTO;
import dev.deyve.grainpayapi.models.Expense;
import org.mapstruct.BeforeMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface ExpenseMapper {

    ExpenseMapper INSTANCE = Mappers.getMapper(ExpenseMapper.class);

    ExpenseDTO toDTO(Expense expense);

    @BeforeMapping
    default void setCreatedAt(ExpenseDTO expenseDTO) {
        expenseDTO.setCreatedAt(LocalDateTime.now());
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(defaultExpression = "java(java.time.LocalDateTime.now())", target = "updatedAt")
    Expense toEntity(ExpenseDTO expenseDTO);
}
