package dev.deyve.grainpayapi.services;

import dev.deyve.grainpayapi.dtos.CreateGoalRequest;
import dev.deyve.grainpayapi.dtos.GoalResponse;
import dev.deyve.grainpayapi.dtos.UpdateGoalRequest;
import dev.deyve.grainpayapi.exceptions.GoalNotFoundException;
import dev.deyve.grainpayapi.models.Goal;
import dev.deyve.grainpayapi.models.User;
import dev.deyve.grainpayapi.repositories.GoalRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class GoalService {

    private static final Logger logger = LoggerFactory.getLogger(GoalService.class);

    private final GoalRepository goalRepository;

    public GoalService(GoalRepository goalRepository) {
        this.goalRepository = goalRepository;
    }

    public List<GoalResponse> findAll(User user) {
        return goalRepository.findAllByUserId(user.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public GoalResponse findById(Long id, User user) {
        return toResponse(findGoalForUser(id, user));
    }

    public GoalResponse save(CreateGoalRequest request, User user) {
        Goal goal = new Goal();
        goal.setName(request.name());
        goal.setTargetAmount(request.targetAmount());
        goal.setDeadline(request.deadline());
        goal.setDescription(request.description());
        goal.setPriority(request.priority());
        goal.setUser(user);

        Goal saved = goalRepository.save(goal);
        logger.debug("GRAIN-API: Goal saved: {}", saved.getId());
        return toResponse(saved);
    }

    public GoalResponse updateById(Long id, UpdateGoalRequest request, User user) {
        Goal existing = findGoalForUser(id, user);

        existing.setName(request.name());
        existing.setTargetAmount(request.targetAmount());
        existing.setCurrentAmount(request.currentAmount());
        existing.setDeadline(request.deadline());
        existing.setDescription(request.description());
        existing.setPriority(request.priority());
        existing.setStatus(request.status());

        Goal updated = goalRepository.save(existing);
        logger.debug("GRAIN-API: Goal updated: {}", updated.getId());
        return toResponse(updated);
    }

    public void deleteById(Long id, User user) {
        findGoalForUser(id, user);
        logger.debug("GRAIN-API: Goal deleted: {}", id);
        goalRepository.deleteById(id);
    }

    private Goal findGoalForUser(Long id, User user) {
        return goalRepository.findById(id)
                .filter(g -> g.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new GoalNotFoundException("Goal not found: " + id));
    }

    private GoalResponse toResponse(Goal goal) {
        BigDecimal progress = goal.getTargetAmount().compareTo(BigDecimal.ZERO) == 0
                ? BigDecimal.ZERO
                : goal.getCurrentAmount()
                       .multiply(new BigDecimal("100"))
                       .divide(goal.getTargetAmount(), 2, RoundingMode.HALF_UP);

        return new GoalResponse(
                goal.getId(),
                goal.getName(),
                goal.getTargetAmount(),
                goal.getCurrentAmount(),
                progress,
                goal.getDeadline(),
                goal.getDescription(),
                goal.getPriority(),
                goal.getStatus(),
                goal.getCreatedAt(),
                goal.getUpdatedAt()
        );
    }
}
