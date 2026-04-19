package dev.deyve.grainpayapi.repositories;

import dev.deyve.grainpayapi.models.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    Page<Account> findAllByUserId(Long userId, Pageable pageable);

    List<Account> findAllByUserIdOrderByNameAsc(Long userId);
}
