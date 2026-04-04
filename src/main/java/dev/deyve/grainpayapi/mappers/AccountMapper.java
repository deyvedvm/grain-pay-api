package dev.deyve.grainpayapi.mappers;

import dev.deyve.grainpayapi.dtos.AccountResponse;
import dev.deyve.grainpayapi.dtos.CreateAccountRequest;
import dev.deyve.grainpayapi.models.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    AccountResponse toResponse(Account account);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Account toEntity(CreateAccountRequest request);
}
