package dev.deyve.grainpayapi.services;

import dev.deyve.grainpayapi.dtos.ImportResultResponse;
import dev.deyve.grainpayapi.dtos.ImportRowError;
import dev.deyve.grainpayapi.models.Transaction;
import dev.deyve.grainpayapi.models.TransactionType;
import dev.deyve.grainpayapi.models.User;
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

    public ImportService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public ImportResultResponse importCsv(MultipartFile file, User user) {
        List<Transaction> toSave = new ArrayList<>();
        List<ImportRowError> errors = new ArrayList<>();

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
                    Transaction transaction = parseRecord(record, user);
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

        logger.info("GRAIN-API: Import finished — imported={}, failed={}", toSave.size(), errors.size());
        return new ImportResultResponse(toSave.size(), errors.size(), errors);
    }

    private Transaction parseRecord(CSVRecord record, User user) {
        String dateStr = getRequired(record, "date", record.getRecordNumber());
        String description = getRequired(record, "description", record.getRecordNumber());
        String amountStr = getRequired(record, "amount", record.getRecordNumber());

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

        return transaction;
    }

    private String getRequired(CSVRecord record, String column, long line) {
        try {
            String value = record.get(column);
            if (value == null || value.isBlank()) {
                throw new IllegalArgumentException("Column '" + column + "' is required at line " + line);
            }
            return value;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException("Missing column '" + column + "' at line " + line);
        }
    }
}
