package dev.deyve.grainpayapi.mappers;

import dev.deyve.grainpayapi.dtos.CategoryResponse;
import dev.deyve.grainpayapi.dtos.CreateCategoryRequest;
import dev.deyve.grainpayapi.models.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryResponse toResponse(Category category);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Category toEntity(CreateCategoryRequest request);
}
