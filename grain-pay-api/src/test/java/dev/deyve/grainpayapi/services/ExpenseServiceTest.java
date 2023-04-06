package dev.deyve.grainpayapi.services;

import dev.deyve.grainpayapi.dtos.ExpenseDTO;
import dev.deyve.grainpayapi.mappers.ExpenseMapper;
import dev.deyve.grainpayapi.models.Expense;
import dev.deyve.grainpayapi.models.PaymentType;
import dev.deyve.grainpayapi.repositories.ExpenseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
@DisplayName("Expense Service Tests")
class ExpenseServiceTest {

    @Mock
    private ExpenseMapper expenseMapper;

    @Mock
    private ExpenseRepository expenseRepository;

    @InjectMocks
    private ExpenseService expenseService;

    private ExpenseDTO expenseDTO;

    private Expense expense;

    @BeforeEach
    void setUp() {
        expenseDTO = ExpenseDTO.builder()
                .id(1L)
                .description("DTO description")
                .amount(BigDecimal.valueOf(100.00))
                .date(LocalDateTime.of(2023, 4, 1, 0, 0))
                .paymentType(PaymentType.MONEY)
                .build();

        expense = Expense.builder()
                .id(1L)
                .description("Expense description")
                .amount(BigDecimal.valueOf(150.00))
                .date(LocalDateTime.of(2023, 4, 2, 0, 0))
                .paymentType(PaymentType.CREDIT_CARD)
                .build();
    }

    @Test
    @DisplayName("Should find expenses")
    void shouldFindExpenses() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id"));

        Page<Expense> expensePage = new PageImpl<>(List.of(expense));

        Page<ExpenseDTO> expenseDTOPage = new PageImpl<>(List.of(expenseDTO));

        // given
        given(expenseRepository.findAll(pageable)).willReturn(expensePage);
        given(expenseMapper.toDTO(expensePage.getContent().get(0))).willReturn(expenseDTO);

        // when
        Page<ExpenseDTO> expenseDTOPageFound = expenseService.findExpenses(pageable);

        // then
        assertEquals(expenseDTOPage, expenseDTOPageFound);
        then(expenseRepository).should().findAll(pageable);
    }

    @Test
    @DisplayName("Should save expense")
    void shouldSaveExpense() {
        Expense expense = Expense.builder().build();

        Expense expenseSaved = Expense.builder().build();

        ExpenseDTO expenseDTO = ExpenseDTO.builder().build();

        // given
        given(expenseMapper.toEntity(expenseDTO)).willReturn(expense);
        given(expenseRepository.save(expense)).willReturn(expenseSaved);
        given(expenseMapper.toDTO(expenseSaved)).willReturn(expenseDTO);

        // when
        ExpenseDTO expenseDTOSaved = expenseService.saveExpense(expenseDTO);

        // then
        assertEquals(expenseDTO, expenseDTOSaved);
        then(expenseRepository).should().save(expense);
    }

    @Test
    @DisplayName("Should find expense by id")
    void shouldFindExpenseById() {
        Long id = 1L;

        // given
        given(expenseRepository.findById(id)).willReturn(Optional.of(expense));
        given(expenseMapper.toDTO(expense)).willReturn(expenseDTO);

        // when
        ExpenseDTO expenseDTOFound = expenseService.findExpenseById(id);

        // then
        assertEquals(expenseDTO, expenseDTOFound);
        then(expenseRepository).should().findById(id);
    }

    @Test
    @DisplayName("Should update expense by id")
    void shouldUpdateExpenseById() {
        Long id = 1L;
        ExpenseDTO expenseDTO = ExpenseDTO.builder()
                .id(id)
                .build();

        Expense expense = Expense.builder().build();

        Expense expenseSaved = Expense.builder().build();

        // given
        given(expenseRepository.findById(id)).willReturn(Optional.of(new Expense()));
        given(expenseMapper.toEntity(expenseDTO)).willReturn(expense);
        given(expenseRepository.save(expense)).willReturn(expenseSaved);
        given(expenseMapper.toDTO(expenseSaved)).willReturn(expenseDTO);

        // when
        expenseService.updateExpenseById(id, expenseDTO);

        // then
        then(expenseRepository).should().findById(id);
        then(expenseRepository).should().save(expense);
    }

    @Test
    @DisplayName("Should delete expense by id")
    void shouldDeleteExpenseById() {
        Long id = 1L;
        // given
        given(expenseRepository.findById(id)).willReturn(Optional.of(new Expense()));

        // when
        expenseService.deleteExpenseById(id);

        // then
        then(expenseRepository).should().deleteById(id);
    }

    @Test
    @DisplayName("Should throw NoSuchElementException when delete expense by id")
    void shouldThrowExceptionWhenDeleteExpenseById() {
        // given
        Long id = 1L;
        given(expenseRepository.findById(id)).willReturn(Optional.empty());

        // when
        try {
            expenseService.deleteExpenseById(id);
        } catch (NoSuchElementException exception) {
            // then
            assertEquals("No value present", exception.getMessage());
        }
    }
}