package dev.deyve.grainpayapi.services;

import dev.deyve.grainpayapi.dtos.ExpenseDTO;
import dev.deyve.grainpayapi.exceptions.ExpenseNotFoundException;
import dev.deyve.grainpayapi.mappers.ExpenseMapper;
import dev.deyve.grainpayapi.models.Expense;
import dev.deyve.grainpayapi.repositories.ExpenseRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static dev.deyve.grainpayapi.dummies.ExpenseDTODummy.buildExpenseDTO;
import static dev.deyve.grainpayapi.dummies.ExpenseDummy.buildExpense;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Expense Service Tests")
class ExpenseServiceTest {

    @Mock
    private ExpenseMapper expenseMapper;

    @Mock
    private ExpenseRepository expenseRepository;

    @InjectMocks
    private ExpenseService expenseService;

    @Test
    @DisplayName("Should find expenses")
    void shouldFindExpenses() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id"));

        ExpenseDTO expenseDTO = buildExpenseDTO().build();

        Page<Expense> expensePage = new PageImpl<>(List.of(buildExpense().build()));

        Page<ExpenseDTO> expenseDTOPage = new PageImpl<>(List.of(expenseDTO));

        when(expenseRepository.findAll(pageable)).thenReturn(expensePage);
        when(expenseMapper.toDTO(expensePage.getContent().get(0))).thenReturn(expenseDTO);

        // Act
        Page<ExpenseDTO> expenseDTOPageFound = expenseService.findAll(pageable);

        // Assert
        assertEquals(expenseDTOPage, expenseDTOPageFound);
        verify(expenseRepository).findAll(pageable);
    }

    @Test
    @DisplayName("Should save expense")
    void shouldSaveExpense() {
        // Arrange
        Expense expense = buildExpense().build();

        Expense expenseSaved = buildExpense().build();

        ExpenseDTO expenseDTO = buildExpenseDTO().build();

        when(expenseMapper.toEntity(expenseDTO)).thenReturn(expense);
        when(expenseRepository.save(expense)).thenReturn(expenseSaved);
        when(expenseMapper.toDTO(expenseSaved)).thenReturn(expenseDTO);

        // Act
        ExpenseDTO expenseDTOSaved = expenseService.save(expenseDTO);

        // Assert
        assertEquals(expenseDTO, expenseDTOSaved);
        verify(expenseRepository).save(expense);
    }

    @Test
    @DisplayName("Should find expense by id")
    void shouldFindExpenseById() throws Throwable {
        // Arrange
        Long id = 1L;
        ExpenseDTO expenseDTO = buildExpenseDTO().build();
        Expense expense = buildExpense().build();

        when(expenseRepository.findById(id)).thenReturn(Optional.of(expense));
        when(expenseMapper.toDTO(expense)).thenReturn(expenseDTO);

        // Act
        ExpenseDTO expenseDTOFound = expenseService.findById(id);

        // Assert
        assertEquals(expenseDTO, expenseDTOFound);
        verify(expenseRepository).findById(id);
    }

    @Test
    @DisplayName("Should throw ExpenseNotFoundException when find expense by id")
    void shouldThrowExceptionWhenFindExpenseById() {
        // Arrange
        Long id = 3L;
        when(expenseRepository.findById(id)).thenThrow(new ExpenseNotFoundException("Expense not found!"));

        // Act and Assert
        assertThrows(ExpenseNotFoundException.class, () -> expenseService.findById(id));
    }

    @Test
    @DisplayName("Should update expense by id")
    void shouldUpdateExpenseById() {
        // Arrange
        Long id = 1L;
        ExpenseDTO expenseDTO = buildExpenseDTO().build();

        Expense expense = buildExpense().build();

        Expense expenseSaved = buildExpense().build();

        when(expenseRepository.findById(id)).thenReturn(Optional.of(buildExpense().build()));
        when(expenseMapper.toEntity(expenseDTO)).thenReturn(expense);
        when(expenseRepository.save(expense)).thenReturn(expenseSaved);
        when(expenseMapper.toDTO(expenseSaved)).thenReturn(expenseDTO);

        // Act
        ExpenseDTO updatedExpenseById = expenseService.updateById(id, expenseDTO);

        // Assert
        assertEquals(updatedExpenseById, expenseDTO);

        verify(expenseRepository).findById(id);
        verify(expenseRepository).save(expense);

    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when update expense by id")
    void sholdThrowExceptionWhenUpdateExpenseById() {
        // Arrange
        ExpenseDTO expenseDTO = buildExpenseDTO().build();

        // Act and Assert
        assertThrows(IllegalArgumentException.class, () -> expenseService.updateById(2L, expenseDTO));
    }

    @Test
    @DisplayName("Should delete expense by id")
    void shouldDeleteExpenseById() {
        // Arrange
        Long id = 1L;
        when(expenseRepository.findById(id)).thenReturn(Optional.of(buildExpense().build()));

        // Act
        expenseService.deleteById(id);

        // Assert
        verify(expenseRepository).deleteById(id);
    }

    @Test
    @DisplayName("Should throw NoSuchElementException when delete expense by id")
    void shouldThrowExceptionWhenDeleteExpenseById() {
        // Arrange
        Long id = 1L;
        when(expenseRepository.findById(id)).thenThrow(new ExpenseNotFoundException("No value present"));

        // Act and Assert
        assertThrows(ExpenseNotFoundException.class, () -> expenseService.deleteById(id));

    }
}