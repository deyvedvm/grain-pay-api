package dev.deyve.grainpayapi.services;

import dev.deyve.grainpayapi.dtos.IncomeDTO;
import dev.deyve.grainpayapi.exceptions.IncomeNotFoundException;
import dev.deyve.grainpayapi.mappers.IncomeMapper;
import dev.deyve.grainpayapi.models.Income;
import dev.deyve.grainpayapi.repositories.IncomeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static dev.deyve.grainpayapi.dummies.IncomeDTODummy.buildIncomeDTO;
import static dev.deyve.grainpayapi.dummies.IncomeDummy.buildIncome;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Income Service Tests")
class IncomeServiceTest {

    @Spy
    private IncomeMapper incomeMapper = Mappers.getMapper(IncomeMapper.class);

    @Mock
    private IncomeRepository incomeRepository;

    @InjectMocks
    private IncomeService incomeService;

    @Test
    @DisplayName("Should find all incomes by page")
    void shouldReturnIncomesByPage() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id"));

        Page<Income> incomePage = new PageImpl<>(List.of(buildIncome().description("Extra").build()));

        Page<IncomeDTO> expectedIncomeDTOPage = new PageImpl<>(List.of(buildIncomeDTO().description("Extra").build()));

        when(incomeRepository.findAll(pageable)).thenReturn(incomePage);

        // Act
        Page<IncomeDTO> incomeDTOPageFound = incomeService.findAll(pageable);

        // Assert
        assertEquals(expectedIncomeDTOPage.getContent().get(0).getDescription(), incomeDTOPageFound.getContent().get(0).getDescription());

        verify(incomeRepository).findAll(pageable);

    }

    @Test
    @DisplayName("Should save income")
    void shouldSaveIncome() {
        // Arrange
        IncomeDTO incomeDTO = buildIncomeDTO().build();

        Income income = buildIncome().amount(BigDecimal.valueOf(500)).build();

        IncomeDTO expectedIncomeDTO = buildIncomeDTO().amount(BigDecimal.valueOf(500)).build();

        when(incomeRepository.save(any())).thenReturn(income);

        // Act
        IncomeDTO incomeDTOsaved = incomeService.save(incomeDTO);

        // Assert
        assertEquals(expectedIncomeDTO.getAmount(), incomeDTOsaved.getAmount());
        verify(incomeRepository).save(income);
    }

    @Test
    @DisplayName("Should return income by id")
    void sholdReturnIncomeById() {
        // Arrange
        Income income = buildIncome().amount(BigDecimal.valueOf(500)).build();

        IncomeDTO expextedIncomeDTO = buildIncomeDTO().amount(BigDecimal.valueOf(500)).build();

        when(incomeRepository.findById(1L)).thenReturn(java.util.Optional.of(income));

        // Act
        IncomeDTO incomeDTOFound = incomeService.findById(1L);

        // Assert
        assertEquals(expextedIncomeDTO.getId(), incomeDTOFound.getId());
        assertEquals(expextedIncomeDTO.getDescription(), incomeDTOFound.getDescription());
        assertEquals(expextedIncomeDTO.getAmount(), incomeDTOFound.getAmount());
        verify(incomeRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw IncomeNotFoundException when find income by id")
    void shouldThrowExceptionWhenFindIncomeById() {
        // Arrange
        when(incomeRepository.findById(3L)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(IncomeNotFoundException.class, () -> incomeService.findById(3L));
    }

    @Test
    @DisplayName("Should update income by id")
    void shouldUpdateIncomeById() {
        // Arrange
        IncomeDTO incomeDTO = buildIncomeDTO().description("Extra salary").build();

        Income income = buildIncome().description("Extra salary").build();

        Income updatedIncome = buildIncome().build();

        IncomeDTO expectedUpdatedIncomeDTO = buildIncomeDTO().build();

        when(incomeRepository.findById(1L)).thenReturn(Optional.of(Income.builder().build()));
        when(incomeRepository.save(income)).thenReturn(updatedIncome);

        // Act
        IncomeDTO updatedIncomeDTO = incomeService.updateById(1L, incomeDTO);

        // Assert
        assertEquals(expectedUpdatedIncomeDTO.getDescription(), updatedIncomeDTO.getDescription());

        verify(incomeRepository).findById(1L);
        verify(incomeRepository).save(any(Income.class));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when update income by id")
    void shouldThrowExceptionWhenUpdateIncomeById() {
        // Arrange
        IncomeDTO incomeDTO = buildIncomeDTO().build();

        // Act and Assert
        assertThrows(IllegalArgumentException.class, () -> incomeService.updateById(2L, incomeDTO));
    }

    @Test
    @DisplayName("Should delete income by id")
    void shouldDeleteIncomeById() {
        // Arrange
        when(incomeRepository.findById(1L)).thenReturn(Optional.of(Income.builder().build()));

        // Act
        incomeService.deleteById(1L);

        // Assert
        verify(incomeRepository).deleteById(1L);

    }

    @Test
    @DisplayName("Should throw NoSuchElementException when delete income by id")
    void shouldThrowExceptionWhenDeleteIncomeById() {
        // Arrange
        when(incomeRepository.findById(1L)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(IncomeNotFoundException.class, () -> incomeService.deleteById(1L));
    }
}