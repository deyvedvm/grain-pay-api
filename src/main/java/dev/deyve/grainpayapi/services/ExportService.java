package dev.deyve.grainpayapi.services;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import dev.deyve.grainpayapi.models.Transaction;
import dev.deyve.grainpayapi.models.User;
import dev.deyve.grainpayapi.repositories.TransactionRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
public class ExportService {

    private static final String[] CSV_HEADERS = {
            "ID", "Date", "Type", "Description", "Amount",
            "Category", "Payment Type", "Account", "Installments", "Current Installment", "Notes"
    };

    private final TransactionRepository transactionRepository;

    public ExportService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public byte[] exportCsv(YearMonth month, User user) throws IOException {
        List<Transaction> transactions = fetchTransactions(month, user);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (CSVPrinter printer = new CSVPrinter(
                new OutputStreamWriter(out, StandardCharsets.UTF_8),
                CSVFormat.DEFAULT.builder().setHeader(CSV_HEADERS).build())) {

            for (Transaction t : transactions) {
                printer.printRecord(
                        t.getId(),
                        t.getDate(),
                        t.getType(),
                        t.getDescription(),
                        t.getAmount(),
                        t.getCategory() != null ? t.getCategory().getName() : "",
                        t.getPaymentType() != null ? t.getPaymentType() : "",
                        t.getAccount() != null ? t.getAccount().getName() : "",
                        t.getInstallments() != null ? t.getInstallments() : "",
                        t.getCurrentInstallment() != null ? t.getCurrentInstallment() : "",
                        t.getNotes() != null ? t.getNotes() : ""
                );
            }
        }
        return out.toByteArray();
    }

    public byte[] exportPdf(YearMonth month, User user) throws DocumentException {
        List<Transaction> transactions = fetchTransactions(month, user);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(document, out);
        document.open();

        document.add(new Paragraph("Transactions — " + month,
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14)));
        document.add(Chunk.NEWLINE);

        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1.5f, 2f, 3f, 2f, 2f, 2f});

        for (String header : new String[]{"Date", "Type", "Description", "Amount", "Category", "Payment Type"}) {
            PdfPCell cell = new PdfPCell(new Phrase(header,
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9)));
            cell.setBackgroundColor(new java.awt.Color(220, 220, 220));
            cell.setPadding(4);
            table.addCell(cell);
        }

        Font rowFont = FontFactory.getFont(FontFactory.HELVETICA, 8);
        for (Transaction t : transactions) {
            table.addCell(new Phrase(t.getDate().toString(), rowFont));
            table.addCell(new Phrase(t.getType().name(), rowFont));
            table.addCell(new Phrase(t.getDescription(), rowFont));
            table.addCell(new Phrase(t.getAmount().toPlainString(), rowFont));
            table.addCell(new Phrase(t.getCategory() != null ? t.getCategory().getName() : "-", rowFont));
            table.addCell(new Phrase(t.getPaymentType() != null ? t.getPaymentType().name() : "-", rowFont));
        }

        document.add(table);
        document.close();
        return out.toByteArray();
    }

    private List<Transaction> fetchTransactions(YearMonth month, User user) {
        LocalDate start = month.atDay(1);
        LocalDate end = month.atEndOfMonth();
        return transactionRepository.findAllByUser_IdAndDateBetweenOrderByDateAsc(user.getId(), start, end);
    }
}
