package dev.deyve.grainpayapi.controllers;

import dev.deyve.grainpayapi.dtos.Response;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface IController<T> {

    ResponseEntity<Response> findAll(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "id") String sort);

    ResponseEntity<Response> post(@Valid @RequestBody T t);

    ResponseEntity<Response> get(@PathVariable Long id);

    ResponseEntity<Response> put(@PathVariable Long id, @Valid @RequestBody T t);

    ResponseEntity<Void> delete(@PathVariable Long id);

}
