package dev.deyve.grainpayapi.services;

import dev.deyve.grainpayapi.dtos.CreateRecurringTransactionRequest;
import dev.deyve.grainpayapi.dtos.RecurringTransactionResponse;
import dev.deyve.grainpayapi.exceptions.AccountNotFoundException;
import dev.deyve.grainpayapi.exceptions.CategoryNotFoundException;
import dev.deyve.grainpayapi.exceptions.RecurringTransactionNotFoundException;
import dev.deyve.grainpayapi.mappers.RecurringTransactionMapper;
import dev.deyve.grainpayapi.models.*;
import dev.deyve.grainpayapi.repositories.AccountRepository;
import dev.deyve.grainpayapi.repositories.CategoryRepository;
import dev.deyve.grainpayapi.repositories.RecurringTransactionRepository;
import dev.deyve.grainpayapi.repositories.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class RecurringTransactionService {

    private static final Logger logger = LoggerFactory.getLogger(RecurringTransactionService.class);

    private final RecurringTransactionRepository recurringTransactionRepository;
    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final AccountRepository accountRepository;
    private final RecurringTransactionMapper mapper;

    public RecurringTransactionService(
            RecurringTransactionRepository recurringTransactionRepository,
            TransactionRepository transactionRepository,
            CategoryRepository categoryRepository,
            AccountRepository accountRepository,
            RecurringTransactionMapper mapper) {
        this.recurringTransactionRepository = recurringTransactionRepository;
        this.transactionRepository = transactionRepository;
        this.categoryRepository = categoryRepository;
        this.accountRepository = accountRepository;
        this.mapper = mapper;
    }

    public Page<RecurringTransactionResponse> findAll(User user, Pageable pageable) {
        return recurringTransactionRepository.findAllByUserId(user.getId(), pageable)
                .map(mapper::toResponse);
    }

    public RecurringTransactionResponse findById(Long id, User user) {
        RecurringTransaction rt = recurringTransactionRepository.findById(id)
                .filter(r -> r.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new RecurringTransactionNotFoundException("Recurring transaction not found: " + id));
        return mapper.toResponse(rt);
    }

    public RecurringTransactionResponse save(CreateRecurringTransactionRequest request, User user) {
        RecurringTransaction rt = mapper.toEntity(request);
        rt.setUser(user);
        rt.setIsActive(true);
        resolveAssociations(rt, request, user);

        RecurringTransaction saved = recurringTransactionRepository.save(rt);
        logger.debug("GRAIN-API: RecurringTransaction saved: {}", saved.getId());
        return mapper.toResponse(saved);
    }

    public RecurringTransactionResponse updateById(Long id, CreateRecurringTransactionRequest request, User user) {
        RecurringTransaction existing = recurringTransactionRepository.findById(id)
                .filter(r -> r.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new RecurringTransactionNotFoundException("Recurring transaction not found: " + id));

        existing.setDescription(request.description());
        existing.setAmount(request.amount());
        existing.setType(request.type());
        existing.setPaymentType(request.paymentType());
        existing.setRecurrenceType(request.recurrenceType());
        existing.setStartDate(request.startDate());
        existing.setEndDate(request.endDate());
        existing.setDayOfMonth(request.dayOfMonth());
        resolveAssociations(existing, request, user);

        RecurringTransaction updated = recurringTransactionRepository.save(existing);
        logger.debug("GRAIN-API: RecurringTransaction updated: {}", updated.getId());
        return mapper.toResponse(updated);
    }

    public void deleteById(Long id, User user) {
        recurringTransactionRepository.findById(id)
                .filter(r -> r.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new RecurringTransactionNotFoundException("Recurring transaction not found: " + id));

        logger.debug("GRAIN-API: RecurringTransaction deleted: {}", id);
        recurringTransactionRepository.deleteById(id);
    }

    @Transactional
    public void materializeForToday(LocalDate today) {
        List<RecurringTransaction> active = recurringTransactionRepository.findAllActiveForDate(today);

        for (RecurringTransaction rt : active) {
            if (!shouldMaterializeToday(rt, today)) continue;

            Transaction tx = new Transaction();
            tx.setType(rt.getType());
            tx.setAmount(rt.getAmount());
            tx.setDate(today);
            tx.setDescription(rt.getDescription());
            tx.setPaymentType(rt.getPaymentType());
            tx.setCategory(rt.getCategory());
            tx.setAccount(rt.getAccount());
            tx.setUser(rt.getUser());
            tx.setIsRecurring(true);

            transactionRepository.save(tx);
            logger.debug("GRAIN-API: Materialized recurring transaction {} for date {}", rt.getId(), today);
        }
    }

    private boolean shouldMaterializeToday(RecurringTransaction rt, LocalDate today) {
        return switch (rt.getRecurrenceType()) {
            case DAILY -> true;
            case WEEKLY -> rt.getStartDate().getDayOfWeek() == today.getDayOfWeek();
            case MONTHLY -> {
                int targetDay = rt.getDayOfMonth() != null ? rt.getDayOfMonth() : rt.getStartDate().getDayOfMonth();
                yield today.getDayOfMonth() == targetDay;
            }
            case YEARLY -> rt.getStartDate().getMonth() == today.getMonth()
                    && rt.getStartDate().getDayOfMonth() == today.getDayOfMonth();
        };
    }

    private void resolveAssociations(RecurringTransaction rt, CreateRecurringTransactionRequest request, User user) {
        if (request.categoryId() != null) {
            Category category = categoryRepository.findById(request.categoryId())
                    .orElseThrow(() -> new CategoryNotFoundException("Category not found: " + request.categoryId()));
            rt.setCategory(category);
        } else {
            rt.setCategory(null);
        }

        if (request.accountId() != null) {
            Account account = accountRepository.findById(request.accountId())
                    .filter(a -> a.getUser().getId().equals(user.getId()))
                    .orElseThrow(() -> new AccountNotFoundException("Account not found: " + request.accountId()));
            rt.setAccount(account);
        } else {
            rt.setAccount(null);
        }
    }
}
