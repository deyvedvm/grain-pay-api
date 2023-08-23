package dev.deyve.grainpayapi.mappers;

import dev.deyve.grainpayapi.dtos.IncomeDTO;
import dev.deyve.grainpayapi.models.Income;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IncomeMapper {

    IncomeDTO toDTO(Income income);

    Income toEntity(IncomeDTO incomeDTO);
}
