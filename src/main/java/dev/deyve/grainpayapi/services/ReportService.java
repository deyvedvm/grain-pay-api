package dev.deyve.grainpayapi.services;

import dev.deyve.grainpayapi.dtos.CategoryReportItem;
import dev.deyve.grainpayapi.dtos.MonthlyReportItem;
import dev.deyve.grainpayapi.dtos.YearlyReportItem;
import dev.deyve.grainpayapi.models.TransactionType;
import dev.deyve.grainpayapi.models.User;
import dev.deyve.grainpayapi.repositories.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportService {

    private final TransactionRepository transactionRepository;

    public ReportService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public List<MonthlyReportItem> getMonthlyReport(Integer year, User user) {
        List<Object[]> rows = transactionRepository.sumByMonthAndTypeForYear(user.getId(), year);

        Map<Integer, BigDecimal[]> byMonth = new HashMap<>();
        for (Object[] row : rows) {
            int month = ((Number) row[0]).intValue();
            TransactionType type = (TransactionType) row[1];
            BigDecimal amount = (BigDecimal) row[2];

            byMonth.computeIfAbsent(month, k -> new BigDecimal[]{BigDecimal.ZERO, BigDecimal.ZERO});
            if (type == TransactionType.INCOME) {
                byMonth.get(month)[0] = amount;
            } else {
                byMonth.get(month)[1] = amount;
            }
        }

        List<MonthlyReportItem> result = new ArrayList<>();
        for (int m = 1; m <= 12; m++) {
            BigDecimal[] totals = byMonth.getOrDefault(m, new BigDecimal[]{BigDecimal.ZERO, BigDecimal.ZERO});
            result.add(new MonthlyReportItem(m, totals[0], totals[1], totals[0].subtract(totals[1])));
        }
        return result;
    }

    public List<YearlyReportItem> getYearlyReport(User user) {
        List<Object[]> rows = transactionRepository.sumByYearAndType(user.getId());

        Map<Integer, BigDecimal[]> byYear = new HashMap<>();
        for (Object[] row : rows) {
            int year = ((Number) row[0]).intValue();
            TransactionType type = (TransactionType) row[1];
            BigDecimal amount = (BigDecimal) row[2];

            byYear.computeIfAbsent(year, k -> new BigDecimal[]{BigDecimal.ZERO, BigDecimal.ZERO});
            if (type == TransactionType.INCOME) {
                byYear.get(year)[0] = amount;
            } else {
                byYear.get(year)[1] = amount;
            }
        }

        return byYear.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> new YearlyReportItem(e.getKey(), e.getValue()[0], e.getValue()[1],
                        e.getValue()[0].subtract(e.getValue()[1])))
                .toList();
    }

    public List<CategoryReportItem> getCategoryReport(YearMonth month, User user) {
        LocalDate start = month.atDay(1);
        LocalDate end = month.atEndOfMonth();

        List<Object[]> rows = transactionRepository.sumByCategoryAndTypeAndDateBetween(user.getId(), start, end);

        Map<String, BigDecimal[]> byCategory = new HashMap<>();
        for (Object[] row : rows) {
            String category = (String) row[0];
            TransactionType type = (TransactionType) row[1];
            BigDecimal amount = (BigDecimal) row[2];

            byCategory.computeIfAbsent(category, k -> new BigDecimal[]{BigDecimal.ZERO, BigDecimal.ZERO});
            if (type == TransactionType.INCOME) {
                byCategory.get(category)[0] = amount;
            } else {
                byCategory.get(category)[1] = amount;
            }
        }

        return byCategory.entrySet().stream()
                .map(e -> new CategoryReportItem(e.getKey(), e.getValue()[0], e.getValue()[1]))
                .toList();
    }
}
