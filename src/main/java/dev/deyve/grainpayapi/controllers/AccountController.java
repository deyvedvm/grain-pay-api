package dev.deyve.grainpayapi.controllers;

import dev.deyve.grainpayapi.dtos.AccountResponse;
import dev.deyve.grainpayapi.dtos.CreateAccountRequest;
import dev.deyve.grainpayapi.dtos.Response;
import dev.deyve.grainpayapi.models.User;
import dev.deyve.grainpayapi.services.AccountService;
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
@RequestMapping("/api/accounts")
public class AccountController {

    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    public ResponseEntity<Response> findAll(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "id") String sort,
            @AuthenticationPrincipal User user) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        logger.info("GRAIN-API: Find accounts page={} size={} sort={}", page, size, sort);

        Page<AccountResponse> accounts = accountService.findAll(user, pageable);
        return new ResponseEntity<>(new Response(accounts.getContent(), OK.value(), "List of accounts"), OK);
    }

    @PostMapping
    public ResponseEntity<Response> post(
            @Valid @RequestBody CreateAccountRequest request,
            @AuthenticationPrincipal User user) {

        logger.info("GRAIN-API: Save account: {}", request.name());
        AccountResponse saved = accountService.save(request, user);
        return new ResponseEntity<>(new Response(saved, CREATED.value(), "Account created"), CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response> get(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {

        logger.info("GRAIN-API: Get account by id {}", id);
        AccountResponse account = accountService.findById(id, user);
        return new ResponseEntity<>(new Response(account, OK.value(), "Account found"), OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Response> put(
            @PathVariable Long id,
            @Valid @RequestBody CreateAccountRequest request,
            @AuthenticationPrincipal User user) {

        logger.info("GRAIN-API: Update account by id {}", id);
        AccountResponse updated = accountService.updateById(id, request, user);
        return new ResponseEntity<>(new Response(updated, OK.value(), "Account updated"), OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Response> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {

        logger.info("GRAIN-API: Delete account by id {}", id);
        accountService.deleteById(id, user);
        return new ResponseEntity<>(new Response(null, NO_CONTENT.value(), "Account deleted"), NO_CONTENT);
    }
}
