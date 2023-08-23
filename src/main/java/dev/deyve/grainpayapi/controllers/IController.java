package dev.deyve.grainpayapi.controllers;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface IController<T> {

    ResponseEntity<List<T>> findAll(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "id") String sort);

    ResponseEntity<T> post(@Valid @RequestBody T t);

    ResponseEntity<T> get(@PathVariable Long id);

    ResponseEntity<T> put(@PathVariable Long id, @Valid @RequestBody T t);

    ResponseEntity<Void> delete(@PathVariable Long id);

}
