package dev.deyve.grainpayapi.controllers;

import dev.deyve.grainpayapi.dtos.ExpenseDTO;
import dev.deyve.grainpayapi.services.ExpenseService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    private static final Logger logger = LoggerFactory.getLogger(ExpenseController.class);

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    /**
     * Get Expenses by Page
     *
     * @return List<ExpenseDTO> List of Expenses
     */
    @GetMapping
    public ResponseEntity<List<ExpenseDTO>> getExpenses(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "id") String sort) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));

        Page<ExpenseDTO> expenses = expenseService.findExpenses(pageable);

        logger.debug("GRAIN-API: Expenses: {}", expenses);

        return new ResponseEntity<>(expenses.getContent(), HttpStatus.OK);
    }

    /**
     * Post Expense
     *
     * @param expenseDTO New Expense
     * @return ResponseEntity
     */
    @PostMapping
    public ResponseEntity<ExpenseDTO> postExpense(@Valid @RequestBody ExpenseDTO expenseDTO) {

        ExpenseDTO expense = expenseService.saveExpense(expenseDTO);

        logger.debug("GRAIN-API: Expense: {}", expense);

        return new ResponseEntity<>(expense, HttpStatus.CREATED);
    }

    /**
     * Get Expense by Id
     *
     * @param id Expense Id
     * @return ResponseEntity
     */
    @GetMapping("/{id}")
    public ResponseEntity<ExpenseDTO> getExpense(@PathVariable Long id) {

        try {
            ExpenseDTO expenseDTO = expenseService.findExpenseById(id);

            logger.debug("GRAIN-API: Expense: {}", expenseDTO);

            return new ResponseEntity<>(expenseDTO, HttpStatus.OK);

        } catch (NoSuchElementException e) {

            logger.error("GRAIN-API: Error finding expense: {}", e.getMessage());

            throw new NoSuchElementException("Expense not found");
        }
    }

    /**
     * Put Expense by Id
     *
     * @param id         Expense Id
     * @param expenseDTO Expense
     * @return ResponseEntity
     */
    @PutMapping("/{id}")
    public ResponseEntity<ExpenseDTO> putExpense(@PathVariable Long id, @Valid @RequestBody ExpenseDTO expenseDTO) {

        try {
            ExpenseDTO updatedExpenseDTO = expenseService.updateExpenseById(id, expenseDTO);

            logger.debug("GRAIN-API: Expense: {}", updatedExpenseDTO);

            return new ResponseEntity<>(updatedExpenseDTO, HttpStatus.OK);

        } catch (IllegalArgumentException e) {

            logger.error("GRAIN-API: Error updating expense: {}", e.getMessage());

            throw new IllegalArgumentException(e.getMessage());

        } catch (NoSuchElementException e) {

            logger.error("GRAIN-API: Error updating expense: {}", e.getMessage());

            throw new NoSuchElementException("Expense not found");
        }
    }


    /**
     * Delete Expense by Id
     *
     * @param id Expense Id
     * @return ResponseEntity
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id) {

        try {

            expenseService.deleteExpenseById(id);

            logger.info("GRAIN-API: Expense id {} deleted", id);

            return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        } catch (NoSuchElementException e) {

            logger.error("GRAIN-API: Error deleting expense id {}: {}", id, e.getMessage());

            throw new NoSuchElementException("Expense not found");
        }
    }
}
