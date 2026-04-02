package dev.deyve.grainpayapi.dtos;

public record AuthResponse(
        String token,
        String email,
        String name
) {
}
