package dev.deyve.grainpayapi.exceptions;

public class IncomeNotFoundException extends RuntimeException {

    public IncomeNotFoundException(String message) {
        super(message);
    }

    public IncomeNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}
