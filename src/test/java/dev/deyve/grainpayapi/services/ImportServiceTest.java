package dev.deyve.grainpayapi.services;

import dev.deyve.grainpayapi.dtos.ImportResultResponse;
import dev.deyve.grainpayapi.models.*;
import dev.deyve.grainpayapi.repositories.AccountRepository;
import dev.deyve.grainpayapi.repositories.CategoryRepository;
import dev.deyve.grainpayapi.repositories.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ImportServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private ImportService importService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);

        when(categoryRepository.findAllByUserIdOrderByNameAsc(1L)).thenReturn(List.of());
        when(accountRepository.findAllByUserIdOrderByNameAsc(1L)).thenReturn(List.of());
        when(transactionRepository.existsByUserIdAndDateAndAmountAndDescription(anyLong(), any(), any(), anyString()))
                .thenReturn(false);
    }

    @Test
    void importCsv_shouldImportValidRows() {
        String csv = "date,description,amount\n2024-01-15,Supermercado,-150.00\n2024-01-16,Salário,3000.00";
        MockMultipartFile file = new MockMultipartFile("file", "extrato.csv", "text/csv", csv.getBytes());

        when(transactionRepository.saveAll(anyList())).thenAnswer(i -> i.getArgument(0));

        ImportResultResponse result = importService.importCsv(file, user);

        assertThat(result.imported()).isEqualTo(2);
        assertThat(result.duplicates()).isEqualTo(0);
        assertThat(result.failed()).isEqualTo(0);
    }

    @Test
    void importCsv_shouldMapNegativeAmountAsExpense() {
        String csv = "date,description,amount\n2024-01-15,Supermercado,-150.00";
        MockMultipartFile file = new MockMultipartFile("file", "extrato.csv", "text/csv", csv.getBytes());

        when(transactionRepository.saveAll(anyList())).thenAnswer(i -> i.getArgument(0));

        importService.importCsv(file, user);

        ArgumentCaptor<List<Transaction>> captor = ArgumentCaptor.forClass(List.class);
        verify(transactionRepository).saveAll(captor.capture());

        Transaction saved = captor.getValue().get(0);
        assertThat(saved.getType()).isEqualTo(TransactionType.EXPENSE);
        assertThat(saved.getAmount()).isEqualByComparingTo("150.00");
    }

    @Test
    void importCsv_shouldMapPositiveAmountAsIncome() {
        String csv = "date,description,amount\n2024-01-16,Salário,3000.00";
        MockMultipartFile file = new MockMultipartFile("file", "extrato.csv", "text/csv", csv.getBytes());

        when(transactionRepository.saveAll(anyList())).thenAnswer(i -> i.getArgument(0));

        importService.importCsv(file, user);

        ArgumentCaptor<List<Transaction>> captor = ArgumentCaptor.forClass(List.class);
        verify(transactionRepository).saveAll(captor.capture());

        Transaction saved = captor.getValue().get(0);
        assertThat(saved.getType()).isEqualTo(TransactionType.INCOME);
        assertThat(saved.getAmount()).isEqualByComparingTo("3000.00");
    }

    @Test
    void importCsv_shouldSkipInvalidRowsAndReportErrors() {
        String csv = "date,description,amount\n2024-01-15,Supermercado,-150.00\ninvalid-date,Algo,100.00\n2024-01-17,Farmácia,-30.00";
        MockMultipartFile file = new MockMultipartFile("file", "extrato.csv", "text/csv", csv.getBytes());

        when(transactionRepository.saveAll(anyList())).thenAnswer(i -> i.getArgument(0));

        ImportResultResponse result = importService.importCsv(file, user);

        assertThat(result.imported()).isEqualTo(2);
        assertThat(result.failed()).isEqualTo(1);
        assertThat(result.errors()).hasSize(1);
        assertThat(result.errors().get(0).line()).isEqualTo(3);
    }

    @Test
    void importCsv_shouldSkipDuplicatesAndCountThem() {
        String csv = "date,description,amount\n2024-01-15,Supermercado,-150.00\n2024-01-16,Salário,3000.00";
        MockMultipartFile file = new MockMultipartFile("file", "extrato.csv", "text/csv", csv.getBytes());

        when(transactionRepository.existsByUserIdAndDateAndAmountAndDescription(
                eq(1L), eq(LocalDate.of(2024, 1, 15)), eq(new BigDecimal("150.00")), eq("Supermercado")))
                .thenReturn(true);
        when(transactionRepository.saveAll(anyList())).thenAnswer(i -> i.getArgument(0));

        ImportResultResponse result = importService.importCsv(file, user);

        assertThat(result.imported()).isEqualTo(1);
        assertThat(result.duplicates()).isEqualTo(1);
        assertThat(result.failed()).isEqualTo(0);
    }

    @Test
    void importCsv_shouldLinkCategoryByDescriptionSubstring() {
        Category alimentacao = new Category();
        alimentacao.setId(1L);
        alimentacao.setName("Alimentação");

        when(categoryRepository.findAllByUserIdOrderByNameAsc(1L)).thenReturn(List.of(alimentacao));

        String csv = "date,description,amount\n2024-01-15,Supermercado alimentação,-80.00";
        MockMultipartFile file = new MockMultipartFile("file", "extrato.csv", "text/csv", csv.getBytes());

        when(transactionRepository.saveAll(anyList())).thenAnswer(i -> i.getArgument(0));

        importService.importCsv(file, user);

        ArgumentCaptor<List<Transaction>> captor = ArgumentCaptor.forClass(List.class);
        verify(transactionRepository).saveAll(captor.capture());

        assertThat(captor.getValue().get(0).getCategory()).isEqualTo(alimentacao);
    }

    @Test
    void importCsv_shouldLinkAccountByDescriptionSubstring() {
        Account nubank = new Account();
        nubank.setId(1L);
        nubank.setName("Nubank");

        when(accountRepository.findAllByUserIdOrderByNameAsc(1L)).thenReturn(List.of(nubank));

        String csv = "date,description,amount\n2024-01-15,Compra Nubank cartão,-200.00";
        MockMultipartFile file = new MockMultipartFile("file", "extrato.csv", "text/csv", csv.getBytes());

        when(transactionRepository.saveAll(anyList())).thenAnswer(i -> i.getArgument(0));

        importService.importCsv(file, user);

        ArgumentCaptor<List<Transaction>> captor = ArgumentCaptor.forClass(List.class);
        verify(transactionRepository).saveAll(captor.capture());

        assertThat(captor.getValue().get(0).getAccount()).isEqualTo(nubank);
    }

    @Test
    void importCsv_shouldLeaveNullWhenNoMatchFound() {
        String csv = "date,description,amount\n2024-01-15,Compra genérica,-50.00";
        MockMultipartFile file = new MockMultipartFile("file", "extrato.csv", "text/csv", csv.getBytes());

        when(transactionRepository.saveAll(anyList())).thenAnswer(i -> i.getArgument(0));

        importService.importCsv(file, user);

        ArgumentCaptor<List<Transaction>> captor = ArgumentCaptor.forClass(List.class);
        verify(transactionRepository).saveAll(captor.capture());

        assertThat(captor.getValue().get(0).getCategory()).isNull();
        assertThat(captor.getValue().get(0).getAccount()).isNull();
    }

    @Test
    void importCsv_shouldRejectZeroAmount() {
        String csv = "date,description,amount\n2024-01-15,Erro,0.00";
        MockMultipartFile file = new MockMultipartFile("file", "extrato.csv", "text/csv", csv.getBytes());

        ImportResultResponse result = importService.importCsv(file, user);

        assertThat(result.imported()).isEqualTo(0);
        assertThat(result.failed()).isEqualTo(1);
        verify(transactionRepository, never()).saveAll(anyList());
    }

    @Test
    void importCsv_shouldThrowWhenFileIsUnreadable() throws Exception {
        MockMultipartFile badFile = mock(MockMultipartFile.class);
        when(badFile.getInputStream()).thenThrow(new java.io.IOException("disk error"));

        assertThatThrownBy(() -> importService.importCsv(badFile, user))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Failed to read CSV file");
    }
}
