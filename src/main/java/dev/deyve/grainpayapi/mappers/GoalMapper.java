package dev.deyve.grainpayapi.mappers;

import dev.deyve.grainpayapi.dtos.CreateGoalRequest;
import dev.deyve.grainpayapi.dtos.GoalResponse;
import dev.deyve.grainpayapi.models.Goal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GoalMapper {

    @Mapping(target = "progress", ignore = true)
    GoalResponse toResponse(Goal goal);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "currentAmount", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Goal toEntity(CreateGoalRequest request);
}
