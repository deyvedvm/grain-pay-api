package dev.deyve.grainpayapi.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.deyve.grainpayapi.dtos.IncomeDTO;
import dev.deyve.grainpayapi.services.IncomeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.util.List;

import static dev.deyve.grainpayapi.dummies.IncomeDTODummy.buildIncomeDTO;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = IncomeController.class)
@DisplayName("Income Controller Tests")
class IncomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IncomeService incomeService;

    @Test
    @DisplayName("Should return a list of incomes")
    void shouldFindAllIncomes() throws Exception {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id"));

        List<IncomeDTO> mockIncomeDTOS = List.of(buildIncomeDTO());

        Page<IncomeDTO> mockIncomesPage = new PageImpl<>(mockIncomeDTOS);

        when(incomeService.findAll(pageable)).thenReturn(mockIncomesPage);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/incomes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].description").value("Salary"))
                .andExpect(jsonPath("$[0].amount").value(BigDecimal.valueOf(1000.00)))
                .andExpect(jsonPath("$[0].date").value("2023-04-01"))
                .andExpect(jsonPath("$[0].createdAt").value("2023-04-01T00:00:00"))
                .andExpect(jsonPath("$[0].updatedAt").value("2023-04-01T00:00:00"));

        verify(incomeService).findAll(pageable);

    }

    @Test
    @DisplayName("Should save a new income and return income saved")
    void shouldPostIncome() throws Exception {
        IncomeDTO mockIncomeDTO = buildIncomeDTO();

        IncomeDTO savedIncomeDTO = buildIncomeDTO();
        savedIncomeDTO.setDescription("Extra Salary");

        when(incomeService.save(mockIncomeDTO)).thenReturn(savedIncomeDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/incomes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockIncomeDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.description").value("Extra Salary"))
                .andExpect(jsonPath("$.amount").value(BigDecimal.valueOf(1000.00)))
                .andExpect(jsonPath("$.date").value("2023-04-01"))
                .andExpect(jsonPath("$.createdAt").value("2023-04-01T00:00:00"))
                .andExpect(jsonPath("$.updatedAt").value("2023-04-01T00:00:00"));

        verify(incomeService).save(mockIncomeDTO);
    }

    @Test
    @DisplayName("Should return income by id")
    void shouldGetIncome() throws Exception {

        IncomeDTO mockIncomeDTO = buildIncomeDTO();

        when(incomeService.findById(1L)).thenReturn(mockIncomeDTO);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/incomes/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Salary"))
                .andExpect(jsonPath("$.amount").value(BigDecimal.valueOf(1000.00)))
                .andExpect(jsonPath("$.date").value("2023-04-01"))
                .andExpect(jsonPath("$.createdAt").value("2023-04-01T00:00:00"))
                .andExpect(jsonPath("$.updatedAt").value("2023-04-01T00:00:00"));

        verify(incomeService).findById(1L);

    }

    @Test
    @DisplayName("Should update income by id")
    void shouldPutIncome() throws Exception {

        IncomeDTO mockIncomeDTO = buildIncomeDTO();

        IncomeDTO updatedIncomeDTO = buildIncomeDTO();
        updatedIncomeDTO.setDescription("Extra Salary");

        when(incomeService.updateById(1L, mockIncomeDTO)).thenReturn(updatedIncomeDTO);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/incomes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockIncomeDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Extra Salary"))
                .andExpect(jsonPath("$.amount").value(BigDecimal.valueOf(1000.00)))
                .andExpect(jsonPath("$.date").value("2023-04-01"))
                .andExpect(jsonPath("$.createdAt").value("2023-04-01T00:00:00"))
                .andExpect(jsonPath("$.updatedAt").value("2023-04-01T00:00:00"));

        verify(incomeService).updateById(1L, mockIncomeDTO);
    }

    @Test
    @DisplayName("Should delete income by id")
    void shouldDeleteIncome() throws Exception {

        doNothing().when(incomeService).deleteById(1L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/incomes/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(incomeService).deleteById(1L);
    }
}

