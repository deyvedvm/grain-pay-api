package dev.deyve.grainpayapi.repositories;

import dev.deyve.grainpayapi.models.Goal;
import dev.deyve.grainpayapi.models.GoalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GoalRepository extends JpaRepository<Goal, Long> {

    List<Goal> findAllByUserId(Long userId);

    List<Goal> findAllByUserIdAndStatus(Long userId, GoalStatus status);
}
