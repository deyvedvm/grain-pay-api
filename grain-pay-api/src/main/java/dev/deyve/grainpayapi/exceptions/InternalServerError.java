package dev.deyve.grainpayapi.exceptions;

public class InternalServerError extends RuntimeException {

    public InternalServerError(String message) {
        super(message);
    }
}
