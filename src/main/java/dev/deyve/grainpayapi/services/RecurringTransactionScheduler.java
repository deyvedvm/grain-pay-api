package dev.deyve.grainpayapi.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class RecurringTransactionScheduler {

    private static final Logger logger = LoggerFactory.getLogger(RecurringTransactionScheduler.class);

    private final RecurringTransactionService recurringTransactionService;

    public RecurringTransactionScheduler(RecurringTransactionService recurringTransactionService) {
        this.recurringTransactionService = recurringTransactionService;
    }

    @Scheduled(cron = "0 5 0 * * *")
    public void materializeRecurringTransactions() {
        LocalDate today = LocalDate.now();
        logger.info("GRAIN-API: Materializing recurring transactions for {}", today);
        recurringTransactionService.materializeForToday(today);
        logger.info("GRAIN-API: Done materializing recurring transactions for {}", today);
    }
}
