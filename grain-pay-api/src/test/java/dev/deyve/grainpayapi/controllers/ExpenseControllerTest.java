package dev.deyve.grainpayapi.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.deyve.grainpayapi.dtos.ExpenseDTO;
import dev.deyve.grainpayapi.models.PaymentType;
import dev.deyve.grainpayapi.repositories.ExpenseRepository;
import dev.deyve.grainpayapi.services.ExpenseService;
import org.junit.jupiter.api.BeforeEach;
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

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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

    @MockBean
    private ExpenseRepository expenseRepository;

    @BeforeEach
    void setUp() {

    }

    @Test
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
                .andDo(print())
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
    void shouldPostExpense() throws Exception {
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
//                .andDo(print())
                .andExpect(status().isCreated());
//                .andExpect(jsonPath("$.description").value("Expense Saved"));

        // then
        verify(expenseService, times(1)).saveExpense(mockExpenseDTO);
    }

    @Test
    void shouldReturnExpenseById() throws Exception {
        Long mockId = 1L;
        ExpenseDTO mockExpenseDTO = ExpenseDTO.builder()
                .id(mockId)
                .description("Mock description")
                .amount(BigDecimal.TEN)
                .build();

        //  given
        given(expenseService.findExpenseById(mockId)).willReturn(mockExpenseDTO);

        //  when
        mockMvc.perform(get("/api/expenses/{id}", mockId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value(mockExpenseDTO.getDescription()))
                .andExpect(jsonPath("$.amount").value(mockExpenseDTO.getAmount()));

        //  then
        verify(expenseService, times(1)).findExpenseById(mockId);
    }

    @Test
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
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("Updated description"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.amount").value(BigDecimal.ONE));

        // then
        verify(expenseService, times(1)).updateExpenseById(mockId, mockExpenseDTO);
    }

    @Test
    void shouldDeleteExpenseAndReturnNoContent() throws Exception {
        Long mockId = 1L;

        //  given
        willDoNothing().given(expenseService).deleteExpenseById(mockId);

        //  when
        mockMvc.perform(delete("/api/expenses/{id}", mockId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());

        //  then
        verify(expenseService, times(1)).deleteExpenseById(1L);
    }
}