package dev.deyve.grainpayapi.services;

import dev.deyve.grainpayapi.dtos.IncomeDTO;
import dev.deyve.grainpayapi.exceptions.IncomeNotFoundException;
import dev.deyve.grainpayapi.mappers.IncomeMapper;
import dev.deyve.grainpayapi.models.Income;
import dev.deyve.grainpayapi.repositories.IncomeRepository;
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

    @Mock
    private IncomeMapper incomeMapper;

    @Mock
    private IncomeRepository incomeRepository;

    @InjectMocks
    private IncomeService incomeService;

    @Test
    @DisplayName("Should find all incomes by page")
    void shouldReturnIncomesByPage() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id"));

        IncomeDTO incomeDTO = buildIncomeDTO().build();

        Page<Income> incomePage = new PageImpl<>(List.of(buildIncome().build()));

        Page<IncomeDTO> incomeDTOPage = new PageImpl<>(List.of(incomeDTO));

        when(incomeRepository.findAll(pageable)).thenReturn(incomePage);
        when(incomeMapper.toDTO(incomePage.getContent().get(0))).thenReturn(incomeDTO);

        // Act
        Page<IncomeDTO> incomeDTOPageFound = incomeService.findAll(pageable);

        // Assert
        assertEquals(incomeDTOPage, incomeDTOPageFound);
        verify(incomeRepository).findAll(pageable);

    }

    @Test
    @DisplayName("Should save income")
    void shouldSaveIncome() {
        // Arrange
        IncomeDTO incomeDTO = buildIncomeDTO().build();

        Income income = buildIncome().build();

        Income savedIncome = buildIncome().id(1L).build();

        when(incomeMapper.toEntity(incomeDTO)).thenReturn(income);
        when(incomeRepository.save(income)).thenReturn(income);
        when(incomeMapper.toDTO(savedIncome)).thenReturn(incomeDTO);

        // Act
        IncomeDTO incomeDTOsaved = incomeService.save(incomeDTO);

        // Assert
        assertEquals(incomeDTO, incomeDTOsaved);
        verify(incomeRepository).save(income);
    }

    @Test
    @DisplayName("Should return income by id")
    void sholdReturnIncomeById() {
        // Arrange
        Income income = buildIncome().build();

        IncomeDTO incomeDTO = buildIncomeDTO().build();

        when(incomeRepository.findById(1L)).thenReturn(java.util.Optional.of(income));
        when(incomeMapper.toDTO(income)).thenReturn(incomeDTO);

        // Act
        IncomeDTO incomeDTOFound = incomeService.findById(1L);

        // Assert
        assertEquals(incomeDTO, incomeDTOFound);
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
        LocalDateTime now = LocalDateTime.now();

        IncomeDTO incomeDTO = buildIncomeDTO().build();

        Income income = buildIncome().build();

        Income updatedIncome = buildIncome().build();

        IncomeDTO expectedUpdatedIncomeDTO = buildIncomeDTO()
                .updatedAt(now)
                .build();

        when(incomeRepository.findById(1L)).thenReturn(Optional.of(Income.builder().build()));
        when(incomeMapper.toEntity(incomeDTO)).thenReturn(income);
        when(incomeRepository.save(income)).thenReturn(updatedIncome);
        when(incomeMapper.toDTO(updatedIncome)).thenReturn(expectedUpdatedIncomeDTO);

        // Act
        IncomeDTO updatedIncomeDTO = incomeService.updateById(1L, incomeDTO);

        // Assert
        assertEquals(expectedUpdatedIncomeDTO, updatedIncomeDTO);
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