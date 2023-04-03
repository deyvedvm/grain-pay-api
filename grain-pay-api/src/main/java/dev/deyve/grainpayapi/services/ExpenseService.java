package dev.deyve.grainpayapi.services;

import dev.deyve.grainpayapi.dtos.ExpenseDTO;
import dev.deyve.grainpayapi.mappers.ExpenseMapper;
import dev.deyve.grainpayapi.models.Expense;
import dev.deyve.grainpayapi.repositories.ExpenseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ExpenseService {

    private static final Logger logger = LoggerFactory.getLogger(ExpenseService.class);

    private final ExpenseRepository expenseRepository;
    private final ExpenseMapper expenseMapper;

    @Autowired
    public ExpenseService(ExpenseRepository expenseRepository, ExpenseMapper expenseMapper) {
        this.expenseRepository = expenseRepository;
        this.expenseMapper = expenseMapper;
    }

    /**
     * Find All Expenses
     *
     * @return Page of ExpenseDTO
     */
    public Page<ExpenseDTO> findExpenses(Pageable pageable) {
        Page<Expense> expenses = expenseRepository.findAll(pageable);

        logger.debug("GRAIN-API: Expenses: {}", expenses);

        return expenses.map(expenseMapper::toDTO);
    }

    /**
     * Save Expense
     *
     * @param expenseDTO ExpenseDTO
     * @return ExpenseDTO
     */
    public ExpenseDTO saveExpense(ExpenseDTO expenseDTO) {
        Expense expense = expenseMapper.toEntity(expenseDTO);

        Expense expenseSaved = expenseRepository.save(expense);

        logger.debug("GRAIN-API: Expense Saved: {}", expenseSaved);

        return expenseMapper.toDTO(expenseSaved);
    }

    /**
     * Find Expense by Id
     *
     * @param id Long
     * @return ExpenseDTO
     */
    public ExpenseDTO findExpenseById(Long id) {
        Expense expense = expenseRepository.findById(id).orElseThrow();

        logger.debug("GRAIN-API: Expense: {}", expense);

        return expenseMapper.toDTO(expense);
    }

    /**
     * Update Expense by Id
     *
     * @param id         Long
     * @param expenseDTO ExpenseDTO
     * @return ExpenseDTO
     */
    public ExpenseDTO updateExpenseById(Long id, ExpenseDTO expenseDTO) {

        checkId(id, expenseDTO);

        expenseRepository.findById(id).orElseThrow();

        Expense expense = expenseMapper.toEntity(expenseDTO);
        expense.setId(id);

        Expense expenseUpdated = expenseRepository.save(expense);

        logger.debug("GRAIN-API: Expense Updated: {}", expenseUpdated);

        return expenseMapper.toDTO(expenseUpdated);
    }


    /**
     * Delete Expense by Id
     *
     * @param id Long
     */
    public void deleteExpenseById(Long id) {

        logger.debug("GRAIN-API: Expense Deleted Id: {}", id);

        expenseRepository.findById(id).orElseThrow();

        expenseRepository.deleteById(id);
    }

    /**
     * Check Id
     *
     * @param id         Long
     * @param expenseDTO ExpenseDTO
     */
    private void checkId(Long id, ExpenseDTO expenseDTO) {
        if (!id.equals(expenseDTO.getId())) {
            throw new IllegalArgumentException("Id is not the same");
        }
    }

}
