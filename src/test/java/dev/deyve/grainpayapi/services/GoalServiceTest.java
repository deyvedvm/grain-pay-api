package dev.deyve.grainpayapi.services;

import dev.deyve.grainpayapi.dtos.CreateGoalRequest;
import dev.deyve.grainpayapi.dtos.GoalResponse;
import dev.deyve.grainpayapi.dtos.UpdateGoalRequest;
import dev.deyve.grainpayapi.exceptions.GoalNotFoundException;
import dev.deyve.grainpayapi.models.Goal;
import dev.deyve.grainpayapi.models.GoalPriority;
import dev.deyve.grainpayapi.models.GoalStatus;
import dev.deyve.grainpayapi.models.User;
import dev.deyve.grainpayapi.repositories.GoalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GoalServiceTest {

    @Mock
    private GoalRepository goalRepository;

    @InjectMocks
    private GoalService goalService;

    private User user;
    private Goal goal;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);

        goal = new Goal();
        goal.setId(1L);
        goal.setName("Emergency Fund");
        goal.setTargetAmount(new BigDecimal("10000.00"));
        goal.setCurrentAmount(new BigDecimal("2500.00"));
        goal.setDeadline(LocalDate.now().plusYears(1));
        goal.setPriority(GoalPriority.HIGH);
        goal.setStatus(GoalStatus.ACTIVE);
        goal.setUser(user);
    }

    @Test
    void findAll_shouldReturnGoalsForUser() {
        when(goalRepository.findAllByUserId(1L)).thenReturn(List.of(goal));

        List<GoalResponse> result = goalService.findAll(user);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("Emergency Fund");
        assertThat(result.get(0).progress()).isEqualByComparingTo("25.00");
    }

    @Test
    void findById_shouldReturnGoalWhenBelongsToUser() {
        when(goalRepository.findById(1L)).thenReturn(Optional.of(goal));

        GoalResponse result = goalService.findById(1L, user);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("Emergency Fund");
    }

    @Test
    void findById_shouldThrowWhenGoalNotFound() {
        when(goalRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> goalService.findById(99L, user))
                .isInstanceOf(GoalNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void findById_shouldThrowWhenGoalBelongsToAnotherUser() {
        User otherUser = new User();
        otherUser.setId(2L);
        goal.setUser(otherUser);

        when(goalRepository.findById(1L)).thenReturn(Optional.of(goal));

        assertThatThrownBy(() -> goalService.findById(1L, user))
                .isInstanceOf(GoalNotFoundException.class);
    }

    @Test
    void save_shouldPersistAndReturnGoal() {
        CreateGoalRequest request = new CreateGoalRequest(
                "New Car",
                new BigDecimal("30000.00"),
                LocalDate.now().plusYears(2),
                "Save for a new car",
                GoalPriority.MEDIUM
        );

        Goal saved = new Goal();
        saved.setId(2L);
        saved.setName("New Car");
        saved.setTargetAmount(new BigDecimal("30000.00"));
        saved.setCurrentAmount(BigDecimal.ZERO);
        saved.setDeadline(request.deadline());
        saved.setPriority(GoalPriority.MEDIUM);
        saved.setStatus(GoalStatus.ACTIVE);
        saved.setUser(user);

        when(goalRepository.save(any(Goal.class))).thenReturn(saved);

        GoalResponse result = goalService.save(request, user);

        assertThat(result.name()).isEqualTo("New Car");
        assertThat(result.progress()).isEqualByComparingTo("0.00");
        assertThat(result.status()).isEqualTo(GoalStatus.ACTIVE);
        verify(goalRepository).save(any(Goal.class));
    }

    @Test
    void updateById_shouldUpdateAndReturnGoal() {
        UpdateGoalRequest request = new UpdateGoalRequest(
                "Emergency Fund",
                new BigDecimal("10000.00"),
                new BigDecimal("5000.00"),
                LocalDate.now().plusYears(1),
                null,
                GoalPriority.HIGH,
                GoalStatus.ACTIVE
        );

        Goal updated = new Goal();
        updated.setId(1L);
        updated.setName("Emergency Fund");
        updated.setTargetAmount(new BigDecimal("10000.00"));
        updated.setCurrentAmount(new BigDecimal("5000.00"));
        updated.setDeadline(request.deadline());
        updated.setPriority(GoalPriority.HIGH);
        updated.setStatus(GoalStatus.ACTIVE);
        updated.setUser(user);

        when(goalRepository.findById(1L)).thenReturn(Optional.of(goal));
        when(goalRepository.save(any(Goal.class))).thenReturn(updated);

        GoalResponse result = goalService.updateById(1L, request, user);

        assertThat(result.currentAmount()).isEqualByComparingTo("5000.00");
        assertThat(result.progress()).isEqualByComparingTo("50.00");
    }

    @Test
    void deleteById_shouldDeleteWhenGoalBelongsToUser() {
        when(goalRepository.findById(1L)).thenReturn(Optional.of(goal));

        goalService.deleteById(1L, user);

        verify(goalRepository).deleteById(1L);
    }

    @Test
    void deleteById_shouldThrowWhenGoalNotFound() {
        when(goalRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> goalService.deleteById(99L, user))
                .isInstanceOf(GoalNotFoundException.class);

        verify(goalRepository, never()).deleteById(any());
    }

    @Test
    void progress_shouldBe100WhenGoalIsComplete() {
        goal.setCurrentAmount(new BigDecimal("10000.00"));
        when(goalRepository.findById(1L)).thenReturn(Optional.of(goal));

        GoalResponse result = goalService.findById(1L, user);

        assertThat(result.progress()).isEqualByComparingTo("100.00");
    }
}
