package dev.deyve.grainpayapi.services;

import dev.deyve.grainpayapi.dtos.CreateTransactionRequest;
import dev.deyve.grainpayapi.dtos.TransactionFilter;
import dev.deyve.grainpayapi.dtos.TransactionResponse;
import dev.deyve.grainpayapi.exceptions.CategoryNotFoundException;
import dev.deyve.grainpayapi.exceptions.TransactionNotFoundException;
import dev.deyve.grainpayapi.mappers.TransactionMapper;
import dev.deyve.grainpayapi.models.Category;
import dev.deyve.grainpayapi.models.Transaction;
import dev.deyve.grainpayapi.models.User;
import dev.deyve.grainpayapi.repositories.CategoryRepository;
import dev.deyve.grainpayapi.repositories.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionMapper transactionMapper;

    public TransactionService(TransactionRepository transactionRepository,
                               CategoryRepository categoryRepository,
                               TransactionMapper transactionMapper) {
        this.transactionRepository = transactionRepository;
        this.categoryRepository = categoryRepository;
        this.transactionMapper = transactionMapper;
    }

    public Page<TransactionResponse> findAll(TransactionFilter filter, User user, Pageable pageable) {
        Specification<Transaction> spec = TransactionSpecification.withFilters(filter, user);
        return transactionRepository.findAll(spec, pageable).map(transactionMapper::toResponse);
    }

    public TransactionResponse save(CreateTransactionRequest request, User user) {
        Transaction transaction = transactionMapper.toEntity(request);
        transaction.setUser(user);

        if (request.categoryId() != null) {
            Category category = categoryRepository.findById(request.categoryId())
                    .filter(c -> c.getUser().getId().equals(user.getId()))
                    .orElseThrow(() -> new CategoryNotFoundException("Category not found: " + request.categoryId()));
            transaction.setCategory(category);
        }

        Transaction saved = transactionRepository.save(transaction);
        logger.debug("GRAIN-API: Transaction saved: {}", saved.getId());

        return transactionMapper.toResponse(saved);
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
