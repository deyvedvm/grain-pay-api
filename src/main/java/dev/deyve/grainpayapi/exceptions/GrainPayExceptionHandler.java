package dev.deyve.grainpayapi.exceptions;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GrainPayExceptionHandler {

    @ExceptionHandler(TransactionNotFoundException.class)
    public ResponseEntity<GrainPayError> handleTransactionNotFound(TransactionNotFoundException ex) {
        return buildError(ex.getMessage(), HttpStatus.NOT_FOUND, List.of());
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<GrainPayError> handleCategoryNotFound(CategoryNotFoundException ex) {
        return buildError(ex.getMessage(), HttpStatus.NOT_FOUND, List.of());
    }

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<GrainPayError> handleAccountNotFound(AccountNotFoundException ex) {
        return buildError(ex.getMessage(), HttpStatus.NOT_FOUND, List.of());
    }

    @ExceptionHandler(RecurringTransactionNotFoundException.class)
    public ResponseEntity<GrainPayError> handleRecurringTransactionNotFound(RecurringTransactionNotFoundException ex) {
        return buildError(ex.getMessage(), HttpStatus.NOT_FOUND, List.of());
    }

    @ExceptionHandler(BudgetNotFoundException.class)
    public ResponseEntity<GrainPayError> handleBudgetNotFound(BudgetNotFoundException ex) {
        return buildError(ex.getMessage(), HttpStatus.NOT_FOUND, List.of());
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<GrainPayError> handleUserAlreadyExists(UserAlreadyExistsException ex) {
        return buildError(ex.getMessage(), HttpStatus.CONFLICT, List.of());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<GrainPayError> handleBadCredentials(BadCredentialsException ex) {
        return buildError("Invalid email or password", HttpStatus.UNAUTHORIZED, List.of());
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<GrainPayError> handleNoSuchElement(NoSuchElementException ex) {
        return buildError(ex.getMessage(), HttpStatus.BAD_REQUEST, List.of());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<GrainPayError> handleIllegalArgument(IllegalArgumentException ex) {
        return buildError(ex.getMessage(), HttpStatus.BAD_REQUEST, List.of());
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<GrainPayError> handleBadRequest(BadRequestException ex) {
        return buildError(ex.getMessage(), HttpStatus.BAD_REQUEST, List.of());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<GrainPayError> handleConstraintViolation(ConstraintViolationException ex) {
        List<String> errors = ex.getConstraintViolations().stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .collect(Collectors.toList());
        return buildError("Constraint violation failed. Check 'errors' field for more details.", HttpStatus.BAD_REQUEST, errors);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<GrainPayError> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.toList());
        return buildError("Validation failed. Check 'errors' field for more details.", HttpStatus.BAD_REQUEST, errors);
    }

    @ExceptionHandler(InternalServerError.class)
    public ResponseEntity<GrainPayError> handleInternalServerError(InternalServerError ex) {
        return buildError(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, List.of());
    }

    private ResponseEntity<GrainPayError> buildError(String message, HttpStatus status, List<String> errors) {
        GrainPayError error = new GrainPayError(message, status.value(), errors, ZonedDateTime.now(ZoneId.of("Z")));
        return new ResponseEntity<>(error, status);
    }
}
