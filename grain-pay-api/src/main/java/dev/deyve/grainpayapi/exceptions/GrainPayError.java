package dev.deyve.grainpayapi.exceptions;

import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;
import java.util.List;

public record GrainPayError(String message, HttpStatus httpStatus, List<String> errors, ZonedDateTime zonedDateTime) {

}

