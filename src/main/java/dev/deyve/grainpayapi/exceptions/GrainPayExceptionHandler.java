package dev.deyve.grainpayapi.exceptions;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @ExceptionHandler(value = {ExpenseNotFoundException.class})
    public ResponseEntity<GrainPayError> handleExpenseNotFoundException(ExpenseNotFoundException expenseNotFoundException) {

        HttpStatus badRequest = HttpStatus.NOT_FOUND;

        GrainPayError grainPayError = new GrainPayError(
                expenseNotFoundException.getMessage(),
                badRequest.value(),
                List.of(),
                ZonedDateTime.now(ZoneId.of("Z"))
        );

        return new ResponseEntity<>(grainPayError, badRequest);
    }

    @ExceptionHandler(value = {IncomeNotFoundException.class})
    public ResponseEntity<GrainPayError> handleIncomeNotFoundException(IncomeNotFoundException incomeNotFoundException) {

        HttpStatus badRequest = HttpStatus.NOT_FOUND;

        GrainPayError grainPayError = new GrainPayError(
                incomeNotFoundException.getMessage(),
                badRequest.value(),
                List.of(),
                ZonedDateTime.now(ZoneId.of("Z"))
        );

        return new ResponseEntity<>(grainPayError, badRequest);
    }

    @ExceptionHandler(value = {NoSuchElementException.class})
    public ResponseEntity<GrainPayError> handleNoSuchElementException(NoSuchElementException noSuchElementException) {

        HttpStatus badRequest = HttpStatus.BAD_REQUEST;

        GrainPayError grainPayError = new GrainPayError(
                noSuchElementException.getMessage(),
                badRequest.value(),
                List.of(),
                ZonedDateTime.now(ZoneId.of("Z"))
        );

        return new ResponseEntity<>(grainPayError, badRequest);
    }

    @ExceptionHandler(value = {IllegalArgumentException.class})
    public ResponseEntity<GrainPayError> handleIllegalArgumentException(IllegalArgumentException illegalArgumentException) {

        HttpStatus badRequest = HttpStatus.BAD_REQUEST;

        GrainPayError grainPayError = new GrainPayError(
                illegalArgumentException.getMessage(),
                badRequest.value(),
                List.of(),
                ZonedDateTime.now(ZoneId.of("Z"))
        );

        return new ResponseEntity<>(grainPayError, badRequest);
    }

    @ExceptionHandler(value = {BadRequestException.class})
    public ResponseEntity<GrainPayError> handleBadRequestException(BadRequestException badRequestException) {

        HttpStatus badRequest = HttpStatus.BAD_REQUEST;

        GrainPayError grainPayError = new GrainPayError(
                badRequestException.getMessage(),
                badRequest.value(),
                List.of(),
                ZonedDateTime.now(ZoneId.of("Z"))
        );

        return new ResponseEntity<>(grainPayError, badRequest);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<GrainPayError> handleConstraintViolationException(ConstraintViolationException constraintViolationException) {
        List<String> errors = constraintViolationException.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .collect(Collectors.toList());

        HttpStatus badRequest = HttpStatus.BAD_REQUEST;

        GrainPayError grainPayError = new GrainPayError(
                "Constraint violation failed. Check 'errors' field for more details.",
                badRequest.value(),
                errors,
                ZonedDateTime.now(ZoneId.of("Z"))
        );

        return new ResponseEntity<>(grainPayError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<GrainPayError> handleMethodArgumentNotValidException(MethodArgumentNotValidException methodArgumentNotValidException) {
        List<String> errors = methodArgumentNotValidException.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.toList());

        HttpStatus badRequest = HttpStatus.BAD_REQUEST;

        GrainPayError grainPayError = new GrainPayError(
                "Validation failed. Check 'errors' field for more details.",
                badRequest.value(),
                errors,
                ZonedDateTime.now(ZoneId.of("Z"))
        );

        return new ResponseEntity<>(grainPayError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {InternalServerError.class})
    public ResponseEntity<GrainPayError> handleInternalServerError(InternalServerError internalServerError) {

        HttpStatus serverError = HttpStatus.INTERNAL_SERVER_ERROR;

        GrainPayError grainPayError = new GrainPayError(
                internalServerError.getMessage(),
                serverError.value(),
                List.of(),
                ZonedDateTime.now(ZoneId.of("Z"))
        );

        return new ResponseEntity<>(grainPayError, serverError);
    }
}
