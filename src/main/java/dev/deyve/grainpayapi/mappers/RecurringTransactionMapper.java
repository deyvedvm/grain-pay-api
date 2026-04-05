package dev.deyve.grainpayapi.mappers;

import dev.deyve.grainpayapi.dtos.CreateRecurringTransactionRequest;
import dev.deyve.grainpayapi.dtos.RecurringTransactionResponse;
import dev.deyve.grainpayapi.models.RecurringTransaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {CategoryMapper.class, AccountMapper.class})
public interface RecurringTransactionMapper {

    @Mapping(source = "category", target = "category")
    @Mapping(source = "account", target = "account")
    RecurringTransactionResponse toResponse(RecurringTransaction recurringTransaction);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "account", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    RecurringTransaction toEntity(CreateRecurringTransactionRequest request);
}
