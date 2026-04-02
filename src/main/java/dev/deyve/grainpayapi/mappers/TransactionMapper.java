package dev.deyve.grainpayapi.mappers;

import dev.deyve.grainpayapi.dtos.CreateTransactionRequest;
import dev.deyve.grainpayapi.dtos.TransactionResponse;
import dev.deyve.grainpayapi.models.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {CategoryMapper.class})
public interface TransactionMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "category", target = "category")
    TransactionResponse toResponse(Transaction transaction);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Transaction toEntity(CreateTransactionRequest request);
}
