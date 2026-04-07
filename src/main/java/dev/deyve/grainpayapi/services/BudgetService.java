package dev.deyve.grainpayapi.services;

import dev.deyve.grainpayapi.dtos.BudgetResponse;
import dev.deyve.grainpayapi.dtos.CategoryResponse;
import dev.deyve.grainpayapi.dtos.CreateBudgetRequest;
import dev.deyve.grainpayapi.exceptions.BudgetNotFoundException;
import dev.deyve.grainpayapi.exceptions.CategoryNotFoundException;
import dev.deyve.grainpayapi.mappers.CategoryMapper;
import dev.deyve.grainpayapi.models.Budget;
import dev.deyve.grainpayapi.models.Category;
import dev.deyve.grainpayapi.models.User;
import dev.deyve.grainpayapi.repositories.BudgetRepository;
import dev.deyve.grainpayapi.repositories.CategoryRepository;
import dev.deyve.grainpayapi.repositories.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
public class BudgetService {

    private static final Logger logger = LoggerFactory.getLogger(BudgetService.class);
    private static final BigDecimal ALERT_THRESHOLD = new BigDecimal("80");

    private final BudgetRepository budgetRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;
    private final CategoryMapper categoryMapper;

    public BudgetService(BudgetRepository budgetRepository,
                         CategoryRepository categoryRepository,
                         TransactionRepository transactionRepository,
                         CategoryMapper categoryMapper) {
        this.budgetRepository = budgetRepository;
        this.categoryRepository = categoryRepository;
        this.transactionRepository = transactionRepository;
        this.categoryMapper = categoryMapper;
    }

    public List<BudgetResponse> findAllByMonth(YearMonth month, User user) {
        return budgetRepository.findAllByUserIdAndMonthAndYear(user.getId(), month.getMonthValue(), month.getYear())
                .stream()
                .map(budget -> toResponse(budget, month))
                .toList();
    }

    public BudgetResponse save(CreateBudgetRequest request, User user) {
        Category category = resolveCategory(request.categoryId(), user);

        budgetRepository.findByUserIdAndCategoryIdAndMonthAndYear(
                user.getId(), request.categoryId(), request.month(), request.year()
        ).ifPresent(b -> {
            throw new IllegalArgumentException(
                    "Budget already exists for category " + request.categoryId() +
                    " in " + request.month() + "/" + request.year());
        });

        Budget budget = new Budget();
        budget.setLimitAmount(request.limitAmount());
        budget.setMonth(request.month());
        budget.setYear(request.year());
        budget.setCategory(category);
        budget.setUser(user);

        Budget saved = budgetRepository.save(budget);
        logger.debug("GRAIN-API: Budget saved: {}", saved.getId());

        YearMonth month = YearMonth.of(saved.getYear(), saved.getMonth());
        return toResponse(saved, month);
    }

    public BudgetResponse updateById(Long id, CreateBudgetRequest request, User user) {
        Budget existing = findBudgetForUser(id, user);
        Category category = resolveCategory(request.categoryId(), user);

        existing.setLimitAmount(request.limitAmount());
        existing.setMonth(request.month());
        existing.setYear(request.year());
        existing.setCategory(category);

        Budget updated = budgetRepository.save(existing);
        logger.debug("GRAIN-API: Budget updated: {}", updated.getId());

        YearMonth month = YearMonth.of(updated.getYear(), updated.getMonth());
        return toResponse(updated, month);
    }

    public void deleteById(Long id, User user) {
        findBudgetForUser(id, user);
        logger.debug("GRAIN-API: Budget deleted: {}", id);
        budgetRepository.deleteById(id);
    }

    private Budget findBudgetForUser(Long id, User user) {
        return budgetRepository.findById(id)
                .filter(b -> b.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new BudgetNotFoundException("Budget not found: " + id));
    }

    private Category resolveCategory(Long categoryId, User user) {
        return categoryRepository.findById(categoryId)
                .filter(c -> c.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new CategoryNotFoundException("Category not found: " + categoryId));
    }

    private BudgetResponse toResponse(Budget budget, YearMonth month) {
        LocalDate start = month.atDay(1);
        LocalDate end = month.atEndOfMonth();

        BigDecimal spent = transactionRepository.sumExpensesByUserAndCategoryAndDateBetween(
                budget.getUser().getId(), budget.getCategory().getId(), start, end);

        BigDecimal percentage = budget.getLimitAmount().compareTo(BigDecimal.ZERO) == 0
                ? BigDecimal.ZERO
                : spent.multiply(new BigDecimal("100"))
                       .divide(budget.getLimitAmount(), 2, RoundingMode.HALF_UP);

        boolean alert = percentage.compareTo(ALERT_THRESHOLD) >= 0;

        CategoryResponse categoryResponse = categoryMapper.toResponse(budget.getCategory());

        return new BudgetResponse(
                budget.getId(),
                categoryResponse,
                budget.getLimitAmount(),
                spent,
                percentage,
                alert,
                budget.getMonth(),
                budget.getYear(),
                budget.getCreatedAt(),
                budget.getUpdatedAt()
        );
    }
}
