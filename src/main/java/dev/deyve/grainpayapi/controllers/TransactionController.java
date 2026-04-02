package dev.deyve.grainpayapi.controllers;

import dev.deyve.grainpayapi.dtos.CreateTransactionRequest;
import dev.deyve.grainpayapi.dtos.Response;
import dev.deyve.grainpayapi.dtos.TransactionFilter;
import dev.deyve.grainpayapi.dtos.TransactionResponse;
import dev.deyve.grainpayapi.models.PaymentType;
import dev.deyve.grainpayapi.models.TransactionType;
import dev.deyve.grainpayapi.models.User;
import dev.deyve.grainpayapi.services.TransactionService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    public ResponseEntity<Response> findAll(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "date") String sort,
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) PaymentType paymentType,
            @RequestParam(required = false) BigDecimal minAmount,
            @RequestParam(required = false) BigDecimal maxAmount,
            @AuthenticationPrincipal User user) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, sort));
        TransactionFilter filter = new TransactionFilter(type, startDate, endDate, categoryId, paymentType, minAmount, maxAmount);

        logger.info("GRAIN-API: Find transactions page={} size={}", page, size);

        Page<TransactionResponse> transactions = transactionService.findAll(filter, user, pageable);
        return new ResponseEntity<>(new Response(transactions.getContent(), OK.value(), "List of transactions"), OK);
    }

    @PostMapping
    public ResponseEntity<Response> post(
            @Valid @RequestBody CreateTransactionRequest request,
            @AuthenticationPrincipal User user) {

        logger.info("GRAIN-API: Save transaction type={}", request.type());
        TransactionResponse saved = transactionService.save(request, user);
        return new ResponseEntity<>(new Response(saved, CREATED.value(), "Transaction created"), CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response> get(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {

        logger.info("GRAIN-API: Get transaction by id {}", id);
        TransactionResponse transaction = transactionService.findById(id, user);
        return new ResponseEntity<>(new Response(transaction, OK.value(), "Transaction found"), OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Response> put(
            @PathVariable Long id,
            @Valid @RequestBody CreateTransactionRequest request,
            @AuthenticationPrincipal User user) {

        logger.info("GRAIN-API: Update transaction by id {}", id);
        TransactionResponse updated = transactionService.updateById(id, request, user);
        return new ResponseEntity<>(new Response(updated, OK.value(), "Transaction updated"), OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Response> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {

        logger.info("GRAIN-API: Delete transaction by id {}", id);
        transactionService.deleteById(id, user);
        return new ResponseEntity<>(new Response(null, NO_CONTENT.value(), "Transaction deleted"), NO_CONTENT);
    }
}
