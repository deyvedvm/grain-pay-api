package dev.deyve.grainpayapi.services;

import dev.deyve.grainpayapi.dtos.AccountResponse;
import dev.deyve.grainpayapi.dtos.CreateAccountRequest;
import dev.deyve.grainpayapi.exceptions.AccountNotFoundException;
import dev.deyve.grainpayapi.mappers.AccountMapper;
import dev.deyve.grainpayapi.models.Account;
import dev.deyve.grainpayapi.models.User;
import dev.deyve.grainpayapi.repositories.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

    private static final Logger logger = LoggerFactory.getLogger(AccountService.class);

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    public AccountService(AccountRepository accountRepository, AccountMapper accountMapper) {
        this.accountRepository = accountRepository;
        this.accountMapper = accountMapper;
    }

    public Page<AccountResponse> findAll(User user, Pageable pageable) {
        return accountRepository.findAllByUserId(user.getId(), pageable)
                .map(accountMapper::toResponse);
    }

    public AccountResponse save(CreateAccountRequest request, User user) {
        Account account = accountMapper.toEntity(request);
        account.setUser(user);

        Account saved = accountRepository.save(account);
        logger.debug("GRAIN-API: Account saved: {}", saved.getId());

        return accountMapper.toResponse(saved);
    }

    public AccountResponse findById(Long id, User user) {
        Account account = accountRepository.findById(id)
                .filter(a -> a.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new AccountNotFoundException("Account not found: " + id));

        return accountMapper.toResponse(account);
    }

    public AccountResponse updateById(Long id, CreateAccountRequest request, User user) {
        Account existing = accountRepository.findById(id)
                .filter(a -> a.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new AccountNotFoundException("Account not found: " + id));

        existing.setName(request.name());
        existing.setType(request.type());
        existing.setBankName(request.bankName());
        existing.setBalance(request.balance());

        Account updated = accountRepository.save(existing);
        logger.debug("GRAIN-API: Account updated: {}", updated.getId());

        return accountMapper.toResponse(updated);
    }

    public void deleteById(Long id, User user) {
        accountRepository.findById(id)
                .filter(a -> a.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new AccountNotFoundException("Account not found: " + id));

        logger.debug("GRAIN-API: Account deleted: {}", id);
        accountRepository.deleteById(id);
    }
}
