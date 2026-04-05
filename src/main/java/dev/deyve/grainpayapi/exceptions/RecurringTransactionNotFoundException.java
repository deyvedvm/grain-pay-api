package dev.deyve.grainpayapi.exceptions;

public class RecurringTransactionNotFoundException extends RuntimeException {

    public RecurringTransactionNotFoundException(String message) {
        super(message);
    }
}
