package dev.deyve.grainpayapi.dtos;

import java.util.List;

public record ImportResultResponse(
        int imported,
        int failed,
        List<ImportRowError> errors
) {
}
