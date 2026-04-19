package dev.deyve.grainpayapi.services;

import dev.deyve.grainpayapi.dtos.ImportResultResponse;
import dev.deyve.grainpayapi.dtos.ImportRowError;
import dev.deyve.grainpayapi.models.*;
import dev.deyve.grainpayapi.repositories.AccountRepository;
import dev.deyve.grainpayapi.repositories.CategoryRepository;
import dev.deyve.grainpayapi.repositories.TransactionRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ImportService {

    private static final Logger logger = LoggerFactory.getLogger(ImportService.class);

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final AccountRepository accountRepository;

    public ImportService(TransactionRepository transactionRepository,
                         CategoryRepository categoryRepository,
                         AccountRepository accountRepository) {
        this.transactionRepository = transactionRepository;
        this.categoryRepository = categoryRepository;
        this.accountRepository = accountRepository;
    }

    @Transactional
    public ImportResultResponse importCsv(MultipartFile file, User user) {
        List<Category> categories = categoryRepository.findAllByUserIdOrderByNameAsc(user.getId());
        List<Account> accounts = accountRepository.findAllByUserIdOrderByNameAsc(user.getId());

        List<Transaction> toSave = new ArrayList<>();
        List<ImportRowError> errors = new ArrayList<>();
        int duplicates = 0;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
             CSVParser parser = CSVFormat.DEFAULT.builder()
                     .setHeader("date", "description", "amount")
                     .setSkipHeaderRecord(true)
                     .setTrim(true)
                     .build()
                     .parse(reader)) {

            int lineNumber = 1;
            for (CSVRecord record : parser) {
                lineNumber++;
                try {
                    Transaction transaction = parseRecord(record, user, categories, accounts);

                    if (isDuplicate(transaction, user)) {
                        duplicates++;
                        logger.debug("GRAIN-API: Duplicate skipped at line {}", lineNumber);
                        continue;
                    }

                    toSave.add(transaction);
                } catch (Exception e) {
                    errors.add(new ImportRowError(lineNumber, e.getMessage()));
                    logger.warn("GRAIN-API: Import error at line {}: {}", lineNumber, e.getMessage());
                }
            }

        } catch (Exception e) {
            logger.error("GRAIN-API: Failed to parse CSV file: {}", e.getMessage());
            throw new IllegalArgumentException("Failed to read CSV file: " + e.getMessage());
        }

        if (!toSave.isEmpty()) {
            transactionRepository.saveAll(toSave);
        }

        logger.info("GRAIN-API: Import finished — imported={}, duplicates={}, failed={}", toSave.size(), duplicates, errors.size());
        return new ImportResultResponse(toSave.size(), duplicates, errors.size(), errors);
    }

    private Transaction parseRecord(CSVRecord record, User user, List<Category> categories, List<Account> accounts) {
        String dateStr = getRequired(record, "date");
        String description = getRequired(record, "description");
        String amountStr = getRequired(record, "amount");

        LocalDate date;
        try {
            date = LocalDate.parse(dateStr);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format '" + dateStr + "', expected yyyy-MM-dd");
        }

        BigDecimal amount;
        try {
            amount = new BigDecimal(amountStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid amount '" + amountStr + "'");
        }

        if (amount.compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalArgumentException("Amount cannot be zero");
        }

        TransactionType type = amount.compareTo(BigDecimal.ZERO) < 0 ? TransactionType.EXPENSE : TransactionType.INCOME;
        BigDecimal absAmount = amount.abs();

        Transaction transaction = new Transaction();
        transaction.setType(type);
        transaction.setAmount(absAmount);
        transaction.setDate(date);
        transaction.setDescription(description);
        transaction.setUser(user);
        transaction.setCategory(matchCategory(description, categories));
        transaction.setAccount(matchAccount(description, accounts));

        return transaction;
    }

    private boolean isDuplicate(Transaction t, User user) {
        return transactionRepository.existsByUserIdAndDateAndAmountAndDescription(
                user.getId(), t.getDate(), t.getAmount(), t.getDescription());
    }

    private Category matchCategory(String description, List<Category> categories) {
        String lower = description.toLowerCase();
        return categories.stream()
                .filter(c -> lower.contains(c.getName().toLowerCase()))
                .findFirst()
                .orElse(null);
    }

    private Account matchAccount(String description, List<Account> accounts) {
        String lower = description.toLowerCase();
        return accounts.stream()
                .filter(a -> lower.contains(a.getName().toLowerCase()))
                .findFirst()
                .orElse(null);
    }

    private String getRequired(CSVRecord record, String column) {
        try {
            String value = record.get(column);
            if (value == null || value.isBlank()) {
                throw new IllegalArgumentException("Column '" + column + "' is required");
            }
            return value;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException("Missing column '" + column + "'");
        }
    }
}
