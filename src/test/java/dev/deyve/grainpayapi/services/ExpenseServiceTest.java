package dev.deyve.grainpayapi.services;

import dev.deyve.grainpayapi.dtos.ExpenseDTO;
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
import static org.mockito.BDDMockito.then;
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
        Page<ExpenseDTO> expenseDTOPageFound = expenseService.findExpenses(pageable);

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
        expenseSaved.setCreatedAt(LocalDateTime.of(2021, 1, 1, 0, 0, 0));

        ExpenseDTO expenseDTO = buildExpenseDTO().build();

        when(expenseMapper.toEntity(expenseDTO)).thenReturn(expense);
        when(expenseRepository.save(expense)).thenReturn(expenseSaved);
        when(expenseMapper.toDTO(expenseSaved)).thenReturn(expenseDTO);

        // Act
        ExpenseDTO expenseDTOSaved = expenseService.saveExpense(expenseDTO);

        // Assert
        assertEquals(expenseDTO, expenseDTOSaved);
        then(expenseRepository).should().save(expense);
    }

    @Test
    @DisplayName("Should find expense by id")
    void shouldFindExpenseById() {
        // Arrange
        Long id = 1L;
        ExpenseDTO expenseDTO = buildExpenseDTO().build();
        Expense expense = buildExpense().build();

        when(expenseRepository.findById(id)).thenReturn(Optional.of(expense));
        when(expenseMapper.toDTO(expense)).thenReturn(expenseDTO);

        // Act
        ExpenseDTO expenseDTOFound = expenseService.findExpenseById(id);

        // Assert
        assertEquals(expenseDTO, expenseDTOFound);
        verify(expenseRepository).findById(id);
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
        ExpenseDTO updatedExpenseById = expenseService.updateExpenseById(id, expenseDTO);

        // Assert
        assertEquals(updatedExpenseById, expenseDTO);

        verify(expenseRepository).findById(id);
        verify(expenseRepository).save(expense);

    }

    @Test
    @DisplayName("Should delete expense by id")
    void shouldDeleteExpenseById() {
        // Arrange
        Long id = 1L;
        when(expenseRepository.findById(id)).thenReturn(Optional.of(buildExpense().build()));

        // Act
        expenseService.deleteExpenseById(id);

        // Assert
        verify(expenseRepository).deleteById(id);
    }

    @Test
    @DisplayName("Should throw NoSuchElementException when delete expense by id")
    void shouldThrowExceptionWhenDeleteExpenseById() {
        // Arrange
        Long id = 1L;
        when(expenseRepository.findById(id)).thenThrow(new NoSuchElementException("No value present"));

        // Act and Assert
        assertThrows(NoSuchElementException.class, () -> expenseService.deleteExpenseById(id));

    }
}