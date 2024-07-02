package dev.deyve.grainpayapi.dtos;

public record Response(
        Object data,
        Integer status,
        String message) {
}
