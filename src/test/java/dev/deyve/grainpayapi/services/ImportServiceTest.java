package dev.deyve.grainpayapi.services;

import dev.deyve.grainpayapi.dtos.ImportResultResponse;
import dev.deyve.grainpayapi.models.Transaction;
import dev.deyve.grainpayapi.models.TransactionType;
import dev.deyve.grainpayapi.models.User;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImportServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private ImportService importService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
    }

    @Test
    void importCsv_shouldImportValidRows() {
        String csv = "date,description,amount\n2024-01-15,Supermercado,-150.00\n2024-01-16,Salário,3000.00";
        MockMultipartFile file = new MockMultipartFile("file", "extrato.csv", "text/csv", csv.getBytes());

        when(transactionRepository.saveAll(anyList())).thenAnswer(i -> i.getArgument(0));

        ImportResultResponse result = importService.importCsv(file, user);

        assertThat(result.imported()).isEqualTo(2);
        assertThat(result.failed()).isEqualTo(0);
        assertThat(result.errors()).isEmpty();
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
    void importCsv_shouldRejectZeroAmount() {
        String csv = "date,description,amount\n2024-01-15,Erro,0.00";
        MockMultipartFile file = new MockMultipartFile("file", "extrato.csv", "text/csv", csv.getBytes());

        ImportResultResponse result = importService.importCsv(file, user);

        assertThat(result.imported()).isEqualTo(0);
        assertThat(result.failed()).isEqualTo(1);
        verify(transactionRepository, never()).saveAll(anyList());
    }

    @Test
    void importCsv_shouldNotCallSaveWhenAllRowsFail() {
        String csv = "date,description,amount\nbad-date,X,abc";
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
