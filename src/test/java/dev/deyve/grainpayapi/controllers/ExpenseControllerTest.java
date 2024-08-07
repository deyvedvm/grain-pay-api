package dev.deyve.grainpayapi.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.deyve.grainpayapi.dtos.ExpenseDTO;
import dev.deyve.grainpayapi.models.PaymentType;
import dev.deyve.grainpayapi.services.ExpenseService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static dev.deyve.grainpayapi.dummies.ExpenseDTODummy.buildExpenseDTO;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ExpenseController.class)
@DisplayName("Expense Controller Tests")
class ExpenseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ExpenseService expenseService;

    @Test
    @DisplayName("Should return a list of expenses")
    void shouldReturnListOfExpenses() throws Exception {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id"));

        List<ExpenseDTO> mockExpenseDTOS = List.of(buildExpenseDTO());

        Page<ExpenseDTO> mockExpensesPage = new PageImpl<>(mockExpenseDTOS);

        when(expenseService.findAll(pageable)).thenReturn(mockExpensesPage);

        mockMvc.perform(get("/api/expenses")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].description").value("Mock description"))
                .andExpect(jsonPath("$.data[0].amount").value(BigDecimal.TEN))
                .andExpect(jsonPath("$.data[0].date").value("2023-04-01T00:00:00"))
                .andExpect(jsonPath("$.data[0].paymentType").value(PaymentType.MONEY.getDescription()));

        verify(expenseService, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Should save expense and return expense saved")
    void shouldSaveExpenseAndReturnExpenseSaved() throws Exception {
        ExpenseDTO mockExpenseDTO = buildExpenseDTO();

        ExpenseDTO savedExpenseDTO = buildExpenseDTO();
        savedExpenseDTO.setDescription("Cellphone");
        savedExpenseDTO.setAmount(BigDecimal.valueOf(1000.00));
        savedExpenseDTO.setCreatedAt(LocalDateTime.of(2023, 4, 1, 0, 0));
        savedExpenseDTO.setUpdatedAt(LocalDateTime.of(2023, 4, 1, 0, 0));

        when(expenseService.save(mockExpenseDTO)).thenReturn(savedExpenseDTO);

        mockMvc.perform(post("/api/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockExpenseDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.description").value("Cellphone"))
                .andExpect(jsonPath("$.data.amount").value(BigDecimal.valueOf(1000.00)))
                .andExpect(jsonPath("$.data.date").value("2023-04-01T00:00:00"))
                .andExpect(jsonPath("$.data.paymentType").value(PaymentType.MONEY.getDescription()))
                .andExpect(jsonPath("$.data.createdAt").value("2023-04-01T00:00:00"))
                .andExpect(jsonPath("$.data.updatedAt").value("2023-04-01T00:00:00"));

        verify(expenseService, times(1)).save(mockExpenseDTO);
    }

    @Test
    @DisplayName("Should return an expense by id")
    void shouldReturnExpenseById() throws Exception {
        ExpenseDTO mockExpenseDTO = buildExpenseDTO();

        Long mockId = mockExpenseDTO.getId();

        when(expenseService.findById(mockId)).thenReturn(mockExpenseDTO);

        mockMvc.perform(get("/api/expenses/{id}", mockId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(mockExpenseDTO.getId()))
                .andExpect(jsonPath("$.data.description").value("Mock description"))
                .andExpect(jsonPath("$.data.amount").value(BigDecimal.TEN))
                .andExpect(jsonPath("$.data.date").value("2023-04-01T00:00:00"))
                .andExpect(jsonPath("$.data.paymentType").value(PaymentType.MONEY.getDescription()));

        verify(expenseService, times(1)).findById(mockId);
    }

    @Test
    @DisplayName("Should update expense by id")
    void shouldUpdateExpenseById() throws Exception {
        ExpenseDTO mockExpenseDTO = buildExpenseDTO();

        Long mockId = mockExpenseDTO.getId();

        ExpenseDTO updatedExpenseDTO = buildExpenseDTO();
        updatedExpenseDTO.setDescription("Updated description");
        updatedExpenseDTO.setCreatedAt(LocalDateTime.of(2023, 4, 1, 0, 0));
        updatedExpenseDTO.setUpdatedAt(LocalDateTime.of(2023, 4, 1, 0, 0));

        when(expenseService.updateById(mockId, mockExpenseDTO)).thenReturn(updatedExpenseDTO);

        mockMvc.perform(put("/api/expenses/{id}", mockId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockExpenseDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.description").value("Updated description"))
                .andExpect(jsonPath("$.data.amount").value(updatedExpenseDTO.getAmount()))
                .andExpect(jsonPath("$.data.date").value("2023-04-01T00:00:00"))
                .andExpect(jsonPath("$.data.paymentType").value(PaymentType.MONEY.getDescription()))
                .andExpect(jsonPath("$.data.createdAt").value("2023-04-01T00:00:00"))
                .andExpect(jsonPath("$.data.updatedAt").value("2023-04-01T00:00:00"));

        verify(expenseService, times(1)).updateById(mockId, mockExpenseDTO);
    }

    @Test
    @DisplayName("Should delete expense and return no content")
    void shouldDeleteExpenseAndReturnNoContent() throws Exception {
        Long mockId = 1L;

        doNothing().when(expenseService).deleteById(mockId);

        mockMvc.perform(delete("/api/expenses/{id}", mockId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(expenseService, times(1)).deleteById(mockId);
    }
}
