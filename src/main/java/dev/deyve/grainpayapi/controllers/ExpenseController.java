package dev.deyve.grainpayapi.controllers;

import dev.deyve.grainpayapi.dtos.ExpenseDTO;
import dev.deyve.grainpayapi.services.ExpenseService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
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

@RestController
@RequestMapping("/api/expenses")
@AllArgsConstructor
public class ExpenseController implements IController<ExpenseDTO> {

    private static final Logger logger = LoggerFactory.getLogger(ExpenseController.class);

    private final ExpenseService expenseService;

    /**
     * Get Expenses by Page
     *
     * @return List<ExpenseDTO> List of Expenses
     */
    @Override
    @GetMapping
    public ResponseEntity<List<ExpenseDTO>> findAll(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "id") String sort) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));

        logger.info("GRAIN-API: Find expenses by page {}, size {} and sort by {}", page, size, sort);

        Page<ExpenseDTO> expenses = expenseService.findAll(pageable);

        return new ResponseEntity<>(expenses.getContent(), HttpStatus.OK);
    }

    /**
     * Post Expense
     *
     * @param expenseDTO New Expense
     * @return ResponseEntity
     */
    @Override
    @PostMapping
    public ResponseEntity<ExpenseDTO> post(@Valid @RequestBody ExpenseDTO expenseDTO) {

        logger.info("GRAIN-API: Save expense: {}", expenseDTO);

        ExpenseDTO expense = expenseService.save(expenseDTO);

        return new ResponseEntity<>(expense, HttpStatus.CREATED);
    }

    /**
     * Get Expense by Id
     *
     * @param id Expense Id
     * @return ResponseEntity
     */
    @Override
    @GetMapping("/{id}")
    public ResponseEntity<ExpenseDTO> get(@PathVariable Long id) {

        logger.info("GRAIN-API: Get expense by id {}", id);

        ExpenseDTO expenseDTO = expenseService.findById(id);

        return new ResponseEntity<>(expenseDTO, HttpStatus.OK);
    }

    /**
     * Put Expense by Id
     *
     * @param id         Expense Id
     * @param expenseDTO Expense
     * @return ResponseEntity
     */
    @Override
    @PutMapping("/{id}")
    public ResponseEntity<ExpenseDTO> put(@PathVariable Long id, @Valid @RequestBody ExpenseDTO expenseDTO) {

        logger.info("GRAIN-API: Update expense by id {}", id);

        ExpenseDTO updatedExpenseDTO = expenseService.updateById(id, expenseDTO);

        return new ResponseEntity<>(updatedExpenseDTO, HttpStatus.OK);

    }

    /**
     * Delete Expense by Id
     *
     * @param id Expense Id
     * @return ResponseEntity
     */
    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        logger.info("GRAIN-API: Delete expense by id {}", id);

        expenseService.deleteById(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
