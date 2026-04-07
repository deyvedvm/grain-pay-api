package dev.deyve.grainpayapi.services;

import dev.deyve.grainpayapi.dtos.CreateTransactionRequest;
import dev.deyve.grainpayapi.dtos.TransactionFilter;
import dev.deyve.grainpayapi.dtos.TransactionResponse;
import dev.deyve.grainpayapi.exceptions.AccountNotFoundException;
import dev.deyve.grainpayapi.exceptions.CategoryNotFoundException;
import dev.deyve.grainpayapi.exceptions.TransactionNotFoundException;
import dev.deyve.grainpayapi.mappers.TransactionMapper;
import dev.deyve.grainpayapi.models.Account;
import dev.deyve.grainpayapi.models.Category;
import dev.deyve.grainpayapi.models.Transaction;
import dev.deyve.grainpayapi.models.User;
import dev.deyve.grainpayapi.repositories.AccountRepository;
import dev.deyve.grainpayapi.repositories.CategoryRepository;
import dev.deyve.grainpayapi.repositories.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
public class TransactionService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final AccountRepository accountRepository;
    private final TransactionMapper transactionMapper;

    public TransactionService(TransactionRepository transactionRepository,
                               CategoryRepository categoryRepository,
                               AccountRepository accountRepository,
                               TransactionMapper transactionMapper) {
        this.transactionRepository = transactionRepository;
        this.categoryRepository = categoryRepository;
        this.accountRepository = accountRepository;
        this.transactionMapper = transactionMapper;
    }

    public Page<TransactionResponse> findAll(TransactionFilter filter, User user, Pageable pageable) {
        Specification<Transaction> spec = TransactionSpecification.withFilters(filter, user);
        return transactionRepository.findAll(spec, pageable).map(transactionMapper::toResponse);
    }

    @Transactional
    public List<TransactionResponse> save(CreateTransactionRequest request, User user) {
        Category category = resolveCategory(request, user);
        Account account = resolveAccount(request, user);

        if (request.installments() != null && request.installments() > 1) {
            return saveInstallments(request, user, category, account);
        }

        Transaction transaction = transactionMapper.toEntity(request);
        transaction.setUser(user);
        transaction.setCategory(category);
        transaction.setAccount(account);

        Transaction saved = transactionRepository.save(transaction);
        logger.debug("GRAIN-API: Transaction saved: {}", saved.getId());

        return List.of(transactionMapper.toResponse(saved));
    }

    private Category resolveCategory(CreateTransactionRequest request, User user) {
        if (request.categoryId() == null) return null;
        return categoryRepository.findById(request.categoryId())
                .filter(c -> c.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new CategoryNotFoundException("Category not found: " + request.categoryId()));
    }

    private Account resolveAccount(CreateTransactionRequest request, User user) {
        if (request.accountId() == null) return null;
        return accountRepository.findById(request.accountId())
                .filter(a -> a.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new AccountNotFoundException("Account not found: " + request.accountId()));
    }

    private List<TransactionResponse> saveInstallments(CreateTransactionRequest request, User user, Category category, Account account) {
        int total = request.installments();
        BigDecimal installmentAmount = request.amount().divide(BigDecimal.valueOf(total), 2, RoundingMode.HALF_UP);

        List<Transaction> installments = new ArrayList<>();
        for (int i = 1; i <= total; i++) {
            Transaction t = transactionMapper.toEntity(request);
            t.setUser(user);
            t.setCategory(category);
            t.setAccount(account);
            t.setCurrentInstallment(i);
            t.setAmount(installmentAmount);
            t.setDate(request.date().plusMonths(i - 1));
            t.setDescription(request.description() + " (" + i + "/" + total + ")");
            installments.add(t);
        }

        List<Transaction> saved = transactionRepository.saveAll(installments);
        logger.debug("GRAIN-API: {} installments saved", saved.size());
        return saved.stream().map(transactionMapper::toResponse).toList();
    }

    public TransactionResponse findById(Long id, User user) {
        Transaction transaction = transactionRepository.findById(id)
                .filter(t -> t.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found: " + id));

        return transactionMapper.toResponse(transaction);
    }

    public TransactionResponse updateById(Long id, CreateTransactionRequest request, User user) {
        Transaction existing = transactionRepository.findById(id)
                .filter(t -> t.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found: " + id));

        existing.setType(request.type());
        existing.setAmount(request.amount());
        existing.setDate(request.date());
        existing.setDescription(request.description());
        existing.setPaymentType(request.paymentType());
        existing.setNotes(request.notes());
        existing.setTags(request.tags());
        existing.setInstallments(request.installments());
        existing.setCurrentInstallment(request.currentInstallment());
        existing.setIsRecurring(request.isRecurring());
        existing.setSource(request.source());

        if (request.categoryId() != null) {
            Category category = categoryRepository.findById(request.categoryId())
                    .filter(c -> c.getUser().getId().equals(user.getId()))
                    .orElseThrow(() -> new CategoryNotFoundException("Category not found: " + request.categoryId()));
            existing.setCategory(category);
        } else {
            existing.setCategory(null);
        }

        if (request.accountId() != null) {
            Account account = accountRepository.findById(request.accountId())
                    .filter(a -> a.getUser().getId().equals(user.getId()))
                    .orElseThrow(() -> new AccountNotFoundException("Account not found: " + request.accountId()));
            existing.setAccount(account);
        } else {
            existing.setAccount(null);
        }

        Transaction updated = transactionRepository.save(existing);
        logger.debug("GRAIN-API: Transaction updated: {}", updated.getId());

        return transactionMapper.toResponse(updated);
    }

    public void deleteById(Long id, User user) {
        transactionRepository.findById(id)
                .filter(t -> t.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found: " + id));

        logger.debug("GRAIN-API: Transaction deleted: {}", id);
        transactionRepository.deleteById(id);
    }
}
