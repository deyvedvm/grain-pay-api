package dev.deyve.grainpayapi.services;

import dev.deyve.grainpayapi.models.Budget;
import dev.deyve.grainpayapi.repositories.BudgetRepository;
import dev.deyve.grainpayapi.repositories.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Component
public class BudgetNotificationScheduler {

    private static final Logger logger = LoggerFactory.getLogger(BudgetNotificationScheduler.class);
    private static final BigDecimal ALERT_THRESHOLD = new BigDecimal("80");

    private final BudgetRepository budgetRepository;
    private final TransactionRepository transactionRepository;
    private final EmailService emailService;

    public BudgetNotificationScheduler(BudgetRepository budgetRepository,
                                       TransactionRepository transactionRepository,
                                       EmailService emailService) {
        this.budgetRepository = budgetRepository;
        this.transactionRepository = transactionRepository;
        this.emailService = emailService;
    }

    @Scheduled(cron = "0 0 9 * * *")
    public void checkBudgetAlerts() {
        YearMonth current = YearMonth.now();
        logger.info("GRAIN-API: Checking budget alerts for {}", current);

        List<Budget> budgets = budgetRepository.findAllByMonthAndYearAndNotifiedFalse(
                current.getMonthValue(), current.getYear());

        for (Budget budget : budgets) {
            LocalDate start = current.atDay(1);
            LocalDate end = current.atEndOfMonth();

            BigDecimal spent = transactionRepository.sumExpensesByUserAndCategoryAndDateBetween(
                    budget.getUser().getId(), budget.getCategory().getId(), start, end);

            if (budget.getLimitAmount().compareTo(BigDecimal.ZERO) == 0) continue;

            BigDecimal percentage = spent.multiply(new BigDecimal("100"))
                    .divide(budget.getLimitAmount(), 2, RoundingMode.HALF_UP);

            if (percentage.compareTo(ALERT_THRESHOLD) >= 0) {
                emailService.sendBudgetAlert(
                        budget.getUser().getEmail(),
                        budget.getUser().getName(),
                        budget.getCategory().getName(),
                        spent,
                        budget.getLimitAmount(),
                        percentage,
                        budget.getMonth(),
                        budget.getYear()
                );
                budget.setNotified(true);
                budgetRepository.save(budget);
            }
        }

        logger.info("GRAIN-API: Budget alert check complete for {}", current);
    }
}
