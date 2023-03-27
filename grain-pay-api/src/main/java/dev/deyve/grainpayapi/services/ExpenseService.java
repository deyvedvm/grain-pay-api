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
     * @return List<ExpenseDTO>
     */
    public Page<ExpenseDTO> findExpenses(Pageable pageable) {
        Page<Expense> expenses = expenseRepository.findAll(pageable);

        logger.debug("GRAIN-API: Expenses: {}", expenses);

        return expenses.map(expenseMapper::toDTO);
    }

    public ExpenseDTO saveExpense(ExpenseDTO expenseDTO) {
        Expense expense = expenseMapper.toEntity(expenseDTO);

        Expense expenseSaved = expenseRepository.save(expense);

        logger.debug("GRAIN-API: Expense Saved: {}", expenseSaved);

        return expenseMapper.toDTO(expenseSaved);
    }

    public ExpenseDTO findExpenseById(Long id) {
        Expense expense = expenseRepository.findById(id).orElseThrow();

        logger.debug("GRAIN-API: Expense: {}", expense);

        return expenseMapper.toDTO(expense);
    }

    public ExpenseDTO updateExpenseById(Long id, ExpenseDTO expenseDTO) {
        expenseRepository.findById(id).orElseThrow();

        Expense expense = expenseMapper.toEntity(expenseDTO);

        Expense expenseUpdated = expenseRepository.save(expense);

        logger.debug("GRAIN-API: Expense Updated: {}", expenseUpdated);

        return expenseMapper.toDTO(expenseUpdated);
    }

    public void deleteExpenseById(Long id) {
        logger.debug("GRAIN-API: Expense Deleted Id: {}", id);

        expenseRepository.deleteById(id);
    }

}
