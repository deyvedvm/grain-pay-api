package dev.deyve.grainpayapi.controllers;

import dev.deyve.grainpayapi.dtos.CreateRecurringTransactionRequest;
import dev.deyve.grainpayapi.dtos.RecurringTransactionResponse;
import dev.deyve.grainpayapi.dtos.Response;
import dev.deyve.grainpayapi.models.User;
import dev.deyve.grainpayapi.services.RecurringTransactionService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/api/recurring-transactions")
public class RecurringTransactionController {

    private static final Logger logger = LoggerFactory.getLogger(RecurringTransactionController.class);

    private final RecurringTransactionService recurringTransactionService;

    public RecurringTransactionController(RecurringTransactionService recurringTransactionService) {
        this.recurringTransactionService = recurringTransactionService;
    }

    @GetMapping
    public ResponseEntity<Response> findAll(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "id") String sort,
            @AuthenticationPrincipal User user) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        logger.info("GRAIN-API: Find recurring transactions page={} size={} sort={}", page, size, sort);

        Page<RecurringTransactionResponse> items = recurringTransactionService.findAll(user, pageable);
        return new ResponseEntity<>(new Response(items.getContent(), OK.value(), "List of recurring transactions"), OK);
    }

    @PostMapping
    public ResponseEntity<Response> post(
            @Valid @RequestBody CreateRecurringTransactionRequest request,
            @AuthenticationPrincipal User user) {

        logger.info("GRAIN-API: Save recurring transaction: {}", request.description());
        RecurringTransactionResponse saved = recurringTransactionService.save(request, user);
        return new ResponseEntity<>(new Response(saved, CREATED.value(), "Recurring transaction created"), CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response> get(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {

        logger.info("GRAIN-API: Get recurring transaction by id {}", id);
        RecurringTransactionResponse rt = recurringTransactionService.findById(id, user);
        return new ResponseEntity<>(new Response(rt, OK.value(), "Recurring transaction found"), OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Response> put(
            @PathVariable Long id,
            @Valid @RequestBody CreateRecurringTransactionRequest request,
            @AuthenticationPrincipal User user) {

        logger.info("GRAIN-API: Update recurring transaction by id {}", id);
        RecurringTransactionResponse updated = recurringTransactionService.updateById(id, request, user);
        return new ResponseEntity<>(new Response(updated, OK.value(), "Recurring transaction updated"), OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Response> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {

        logger.info("GRAIN-API: Delete recurring transaction by id {}", id);
        recurringTransactionService.deleteById(id, user);
        return new ResponseEntity<>(new Response(null, NO_CONTENT.value(), "Recurring transaction deleted"), NO_CONTENT);
    }
}
