package dev.deyve.grainpayapi.controllers;

import dev.deyve.grainpayapi.dtos.ExpenseDTO;
import dev.deyve.grainpayapi.dtos.Response;
import dev.deyve.grainpayapi.services.ExpenseService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController implements IController<ExpenseDTO> {

    private static final Logger logger = LoggerFactory.getLogger(ExpenseController.class);

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    /**
     * Get Expenses by Page
     *
     * @param page number of page
     * @param size number of items
     * @param sort sort by
     * @return ResponseEntity
     */
    @Override
    @GetMapping
    public ResponseEntity<Response> findAll(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "id") String sort) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));

        logger.info("GRAIN-API: Find expenses by page {}, size {} and sort by {}", page, size, sort);

        Page<ExpenseDTO> expenses = expenseService.findAll(pageable);

        return new ResponseEntity<>(new Response(expenses.getContent(), OK.value(), "List of expenses"), OK);
    }

    /**
     * Post Expense
     *
     * @param expenseDTO New Expense
     * @return ResponseEntity
     */
    @Override
    @PostMapping
    public ResponseEntity<Response> post(@Valid @RequestBody ExpenseDTO expenseDTO) {

        logger.info("GRAIN-API: Save expense: {}", expenseDTO);

        ExpenseDTO expense = expenseService.save(expenseDTO);

        return new ResponseEntity<>(new Response(expense, CREATED.value(), "Expense created"), CREATED);
    }

    /**
     * Get Expense by Id
     *
     * @param id Expense Id
     * @return ResponseEntity
     */
    @Override
    @GetMapping("/{id}")
    public ResponseEntity<Response> get(@PathVariable Long id) {

        logger.info("GRAIN-API: Get expense by id {}", id);

        ExpenseDTO expenseDTO = expenseService.findById(id);

        return new ResponseEntity<>(new Response(expenseDTO, OK.value(), "Expense found"), OK);
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
    public ResponseEntity<Response> put(@PathVariable Long id, @Valid @RequestBody ExpenseDTO expenseDTO) {

        logger.info("GRAIN-API: Update expense by id {}", id);

        ExpenseDTO updatedExpenseDTO = expenseService.updateById(id, expenseDTO);

        return new ResponseEntity<>(new Response(updatedExpenseDTO, OK.value(), "Expense updated"), OK);

    }

    /**
     * Delete Expense by Id
     *
     * @param id Expense Id
     * @return ResponseEntity
     */
    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Response> delete(@PathVariable Long id) {

        logger.info("GRAIN-API: Delete expense by id {}", id);

        expenseService.deleteById(id);

        return new ResponseEntity<>(new Response(null, NO_CONTENT.value(), "Expense deleted"), NO_CONTENT);
    }

}
