package dev.deyve.grainpayapi.repositories;

import dev.deyve.grainpayapi.models.Transaction;
import dev.deyve.grainpayapi.models.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long>, JpaSpecificationExecutor<Transaction> {

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
            "WHERE t.user.id = :userId AND t.type = :type AND t.date BETWEEN :start AND :end")
    BigDecimal sumByUserAndTypeAndDateBetween(@Param("userId") Long userId,
                                              @Param("type") TransactionType type,
                                              @Param("start") LocalDate start,
                                              @Param("end") LocalDate end);

    @Query("SELECT COALESCE(t.category.name, 'Sem categoria'), SUM(t.amount) FROM Transaction t " +
            "WHERE t.user.id = :userId AND t.type = 'EXPENSE' AND t.date BETWEEN :start AND :end " +
            "GROUP BY t.category.name")
    List<Object[]> sumExpensesByCategoryAndDateBetween(@Param("userId") Long userId,
                                                       @Param("start") LocalDate start,
                                                       @Param("end") LocalDate end);

    @Query("SELECT t.source, SUM(t.amount) FROM Transaction t " +
            "WHERE t.user.id = :userId AND t.type = 'INCOME' AND t.date BETWEEN :start AND :end " +
            "AND t.source IS NOT NULL GROUP BY t.source")
    List<Object[]> sumIncomeBySourceAndDateBetween(@Param("userId") Long userId,
                                                   @Param("start") LocalDate start,
                                                   @Param("end") LocalDate end);
}
