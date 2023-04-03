package dev.deyve.grainpayapi.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.deyve.grainpayapi.dtos.ExpenseDTO;
import dev.deyve.grainpayapi.models.PaymentType;
import dev.deyve.grainpayapi.services.ExpenseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ExpenseController.class)
class ExpenseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ExpenseService expenseService;

    @BeforeEach
    void setUp() {

    }

    @Test
    @DisplayName("Should return a list of expenses")
    void shouldReturnListOfExpenses() throws Exception {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id"));

        List<ExpenseDTO> mockExpenseDTOS = List.of(ExpenseDTO.builder()
                .description("Mock description")
                .amount(BigDecimal.TEN)
                .date(LocalDateTime.of(2023, 4, 1, 0, 0))
                .paymentType(PaymentType.MONEY)
                .build());

        Page<ExpenseDTO> mockExpensesPage = new PageImpl<>(mockExpenseDTOS);

        //  given
        given(expenseService.findExpenses(pageable)).willReturn(mockExpensesPage);

        //  when
        mockMvc.perform(get("/api/expenses")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].description").value("Mock description"))
                .andExpect(jsonPath("$[0].amount").value(BigDecimal.TEN))
                .andExpect(jsonPath("$[0].date").value("2023-04-01T00:00:00"))
                .andExpect(jsonPath("$[0].paymentType").value(PaymentType.MONEY.getDescription()));

        //  then
        verify(expenseService, times(1)).findExpenses(pageable);
    }

    @Test
    @DisplayName("Should save expense and return expense saved")
    void shouldSaveExpenseAndReturnExpenseSaved() throws Exception {
        ExpenseDTO mockExpenseDTO = ExpenseDTO.builder()
                .description("Celphone")
                .amount(BigDecimal.valueOf(1000.00))
                .date(LocalDateTime.of(2023, 4, 1, 0, 0))
                .paymentType(PaymentType.MONEY)
                .build();

        ExpenseDTO savedExpenseDTO = ExpenseDTO.builder()
                .id(1L)
                .description("Celphone")
                .amount(BigDecimal.valueOf(1000.00))
                .date(LocalDateTime.of(2023, 4, 1, 0, 0))
                .paymentType(PaymentType.MONEY)
                .build();

        // given
        given(expenseService.saveExpense(mockExpenseDTO)).willReturn(savedExpenseDTO);

        // when
        mockMvc.perform(post("/api/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockExpenseDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("Celphone"))
                .andExpect(jsonPath("$.amount").value(BigDecimal.valueOf(1000.00)))
                .andExpect(jsonPath("$.date").value("2023-04-01T00:00:00"))
                .andExpect(jsonPath("$.paymentType").value(PaymentType.MONEY.getDescription()));

        // then
        verify(expenseService, times(1)).saveExpense(mockExpenseDTO);
    }

    @Test
    @DisplayName("Should return a expense by id")
    void shouldReturnExpenseById() throws Exception {
        Long mockId = 1L;
        ExpenseDTO mockExpenseDTO = ExpenseDTO.builder()
                .id(mockId)
                .description("Mock description")
                .amount(BigDecimal.TEN)
                .date(LocalDateTime.of(2023, 4, 1, 0, 0))
                .paymentType(PaymentType.MONEY)
                .build();

        //  given
        given(expenseService.findExpenseById(mockId)).willReturn(mockExpenseDTO);

        //  when
        mockMvc.perform(get("/api/expenses/{id}", mockId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value(mockExpenseDTO.getDescription()))
                .andExpect(jsonPath("$.amount").value(mockExpenseDTO.getAmount()))
                .andExpect(jsonPath("$.date").value("2023-04-01T00:00:00"))
                .andExpect(jsonPath("$.paymentType").value(mockExpenseDTO.getPaymentType().getDescription()));

        //  then
        verify(expenseService, times(1)).findExpenseById(mockId);
    }

    @Test
    @DisplayName("Should return a expense updated")
    void shouldUpdateExpenseById() throws Exception {
        Long mockId = 1L;
        ExpenseDTO mockExpenseDTO = ExpenseDTO.builder()
                .id(mockId)
                .description("Mock description")
                .amount(BigDecimal.TEN)
                .date(LocalDateTime.of(2023, 4, 1, 0, 0))
                .paymentType(PaymentType.MONEY)
                .build();

        ExpenseDTO updatedExpenseDTO = ExpenseDTO.builder()
                .id(mockId)
                .description("Updated description")
                .amount(BigDecimal.ONE)
                .date(LocalDateTime.of(2023, 4, 1, 0, 0))
                .paymentType(PaymentType.MONEY)
                .build();

        // given
        given(expenseService.updateExpenseById(mockId, mockExpenseDTO)).willReturn(updatedExpenseDTO);

        // when
        mockMvc.perform(MockMvcRequestBuilders.put("/api/expenses/{id}", mockId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockExpenseDTO)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("Updated description"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.amount").value(updatedExpenseDTO.getAmount()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.date").value("2023-04-01T00:00:00"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.paymentType").value(PaymentType.MONEY.getDescription()));

        // then
        verify(expenseService, times(1)).updateExpenseById(mockId, mockExpenseDTO);
    }

    @Test
    @DisplayName("Should delete expense and return no content")
    void shouldDeleteExpenseAndReturnNoContent() throws Exception {
        Long mockId = 1L;

        //  given
        willDoNothing().given(expenseService).deleteExpenseById(mockId);

        //  when
        mockMvc.perform(delete("/api/expenses/{id}", mockId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        //  then
        verify(expenseService, times(1)).deleteExpenseById(1L);
    }

    @Test
    @DisplayName("Should throw exception when try to delete expense")
    void shouldThrowExceptionWhenTryToDeleteExpense() throws Exception {
        Long mockId = 2L;

        //  given
        willThrow(NoSuchElementException.class).given(expenseService).deleteExpenseById(mockId);

        //  when
        mockMvc.perform(delete("/api/expenses/{id}", mockId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        //  then
        verify(expenseService, times(1)).deleteExpenseById(2L);
    }
}