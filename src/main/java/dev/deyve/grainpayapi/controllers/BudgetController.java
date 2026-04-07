package dev.deyve.grainpayapi.controllers;

import dev.deyve.grainpayapi.dtos.BudgetResponse;
import dev.deyve.grainpayapi.dtos.CreateBudgetRequest;
import dev.deyve.grainpayapi.dtos.Response;
import dev.deyve.grainpayapi.models.User;
import dev.deyve.grainpayapi.services.BudgetService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.List;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/api/budgets")
public class BudgetController {

    private static final Logger logger = LoggerFactory.getLogger(BudgetController.class);

    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    @GetMapping
    public ResponseEntity<Response> findAll(
            @RequestParam YearMonth month,
            @AuthenticationPrincipal User user) {

        logger.info("GRAIN-API: Find budgets month={}", month);
        List<BudgetResponse> budgets = budgetService.findAllByMonth(month, user);
        return new ResponseEntity<>(new Response(budgets, OK.value(), "List of budgets"), OK);
    }

    @PostMapping
    public ResponseEntity<Response> post(
            @Valid @RequestBody CreateBudgetRequest request,
            @AuthenticationPrincipal User user) {

        logger.info("GRAIN-API: Save budget categoryId={}", request.categoryId());
        BudgetResponse saved = budgetService.save(request, user);
        return new ResponseEntity<>(new Response(saved, CREATED.value(), "Budget created"), CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Response> put(
            @PathVariable Long id,
            @Valid @RequestBody CreateBudgetRequest request,
            @AuthenticationPrincipal User user) {

        logger.info("GRAIN-API: Update budget id={}", id);
        BudgetResponse updated = budgetService.updateById(id, request, user);
        return new ResponseEntity<>(new Response(updated, OK.value(), "Budget updated"), OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Response> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {

        logger.info("GRAIN-API: Delete budget id={}", id);
        budgetService.deleteById(id, user);
        return new ResponseEntity<>(new Response(null, NO_CONTENT.value(), "Budget deleted"), NO_CONTENT);
    }
}
