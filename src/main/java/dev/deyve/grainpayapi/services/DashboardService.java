package dev.deyve.grainpayapi.services;

import dev.deyve.grainpayapi.dtos.CategorySummary;
import dev.deyve.grainpayapi.dtos.DashboardSummaryResponse;
import dev.deyve.grainpayapi.dtos.SourceSummary;
import dev.deyve.grainpayapi.models.IncomeSource;
import dev.deyve.grainpayapi.models.TransactionType;
import dev.deyve.grainpayapi.models.User;
import dev.deyve.grainpayapi.repositories.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
public class DashboardService {

    private final TransactionRepository transactionRepository;

    public DashboardService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public DashboardSummaryResponse getSummary(YearMonth month, User user) {
        LocalDate start = month.atDay(1);
        LocalDate end = month.atEndOfMonth();
        Long userId = user.getId();

        BigDecimal totalIncome = transactionRepository.sumByUserAndTypeAndDateBetween(userId, TransactionType.INCOME, start, end);
        BigDecimal totalExpenses = transactionRepository.sumByUserAndTypeAndDateBetween(userId, TransactionType.EXPENSE, start, end);
        BigDecimal balance = totalIncome.subtract(totalExpenses);

        List<CategorySummary> expensesByCategory = transactionRepository
                .sumExpensesByCategoryAndDateBetween(userId, start, end)
                .stream()
                .map(row -> new CategorySummary((String) row[0], (BigDecimal) row[1]))
                .toList();

        List<SourceSummary> incomeBySource = transactionRepository
                .sumIncomeBySourceAndDateBetween(userId, start, end)
                .stream()
                .map(row -> new SourceSummary((IncomeSource) row[0], (BigDecimal) row[1]))
                .toList();

        return new DashboardSummaryResponse(totalIncome, totalExpenses, balance, expensesByCategory, incomeBySource);
    }
}
