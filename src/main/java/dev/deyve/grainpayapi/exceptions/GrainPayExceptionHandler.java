package dev.deyve.grainpayapi.exceptions;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpServerErrorException;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GrainPayExceptionHandler {

    @ExceptionHandler(value = {ExpenseNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleExpenseNotFoundException(ExpenseNotFoundException expenseNotFoundException) {

        HttpStatus notFound = HttpStatus.NOT_FOUND;

        ErrorResponse errorResponse = new ErrorResponse(
                expenseNotFoundException.getMessage(),
                notFound,
                List.of(),
                ZonedDateTime.now(ZoneId.of("Z"))
        );

        return new ResponseEntity<>(errorResponse, notFound);
    }

    @ExceptionHandler(value = {IncomeNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleIncomeNotFoundException(IncomeNotFoundException incomeNotFoundException) {

        HttpStatus notFound = HttpStatus.NOT_FOUND;

        ErrorResponse errorResponse = new ErrorResponse(
                incomeNotFoundException.getMessage(),
                notFound,
                List.of(),
                ZonedDateTime.now(ZoneId.of("Z"))
        );

        return new ResponseEntity<>(errorResponse, notFound);
    }

    @ExceptionHandler(value = {NoSuchElementException.class})
    public ResponseEntity<ErrorResponse> handleNoSuchElementException(NoSuchElementException noSuchElementException) {

        HttpStatus badRequest = HttpStatus.BAD_REQUEST;

        ErrorResponse errorResponse = new ErrorResponse(
                noSuchElementException.getMessage(),
                badRequest,
                List.of(),
                ZonedDateTime.now(ZoneId.of("Z"))
        );

        return new ResponseEntity<>(errorResponse, badRequest);
    }

    @ExceptionHandler(value = {IllegalArgumentException.class})
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException illegalArgumentException) {

        HttpStatus badRequest = HttpStatus.BAD_REQUEST;

        ErrorResponse errorResponse = new ErrorResponse(
                illegalArgumentException.getMessage(),
                badRequest,
                List.of(),
                ZonedDateTime.now(ZoneId.of("Z"))
        );

        return new ResponseEntity<>(errorResponse, badRequest);
    }

    @ExceptionHandler(value = {BadRequestException.class})
    public ResponseEntity<ErrorResponse> handleBadRequestException(BadRequestException badRequestException) {

        HttpStatus badRequest = HttpStatus.BAD_REQUEST;

        ErrorResponse errorResponse = new ErrorResponse(
                badRequestException.getMessage(),
                badRequest,
                List.of(),
                ZonedDateTime.now(ZoneId.of("Z"))
        );

        return new ResponseEntity<>(errorResponse, badRequest);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException constraintViolationException) {
        List<String> errors = constraintViolationException.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .collect(Collectors.toList());

        HttpStatus badRequest = HttpStatus.BAD_REQUEST;

        ErrorResponse errorResponse = new ErrorResponse(
                "Constraint violation failed. Check 'errors' field for more details.",
                badRequest,
                errors,
                ZonedDateTime.now(ZoneId.of("Z"))
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException methodArgumentNotValidException) {
        List<String> errors = methodArgumentNotValidException.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.toList());

        HttpStatus badRequest = HttpStatus.BAD_REQUEST;

        ErrorResponse errorResponse = new ErrorResponse(
                "Validation failed. Check 'errors' field for more details.",
                badRequest,
                errors,
                ZonedDateTime.now(ZoneId.of("Z"))
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {HttpServerErrorException.InternalServerError.class})
    public ResponseEntity<ErrorResponse> handleInternalServerError(HttpServerErrorException.InternalServerError internalServerError) {

        HttpStatus serverError = HttpStatus.INTERNAL_SERVER_ERROR;

        ErrorResponse errorResponse = new ErrorResponse(
                internalServerError.getMessage(),
                serverError,
                List.of(),
                ZonedDateTime.now(ZoneId.of("Z"))
        );

        return new ResponseEntity<>(errorResponse, serverError);
    }

}
