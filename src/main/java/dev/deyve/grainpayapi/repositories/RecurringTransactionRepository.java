package dev.deyve.grainpayapi.repositories;

import dev.deyve.grainpayapi.models.RecurringTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface RecurringTransactionRepository extends JpaRepository<RecurringTransaction, Long> {

    Page<RecurringTransaction> findAllByUserId(Long userId, Pageable pageable);

    @Query("""
            SELECT r FROM RecurringTransaction r
            WHERE r.isActive = true
              AND r.startDate <= :today
              AND (r.endDate IS NULL OR r.endDate >= :today)
            """)
    List<RecurringTransaction> findAllActiveForDate(@Param("today") LocalDate today);
}
