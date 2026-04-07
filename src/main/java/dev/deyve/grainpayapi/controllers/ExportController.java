package dev.deyve.grainpayapi.controllers;

import com.lowagie.text.DocumentException;
import dev.deyve.grainpayapi.exceptions.InternalServerError;
import dev.deyve.grainpayapi.models.User;
import dev.deyve.grainpayapi.services.ExportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.YearMonth;

@RestController
@RequestMapping("/api/export")
public class ExportController {

    private static final Logger logger = LoggerFactory.getLogger(ExportController.class);

    private final ExportService exportService;

    public ExportController(ExportService exportService) {
        this.exportService = exportService;
    }

    @GetMapping("/csv")
    public ResponseEntity<byte[]> exportCsv(
            @RequestParam YearMonth month,
            @AuthenticationPrincipal User user) {

        logger.info("GRAIN-API: Export CSV month={}", month);
        try {
            byte[] content = exportService.exportCsv(month, user);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=transactions-" + month + ".csv")
                    .contentType(MediaType.parseMediaType("text/csv"))
                    .body(content);
        } catch (IOException e) {
            throw new InternalServerError("Failed to generate CSV: " + e.getMessage());
        }
    }

    @GetMapping("/pdf")
    public ResponseEntity<byte[]> exportPdf(
            @RequestParam YearMonth month,
            @AuthenticationPrincipal User user) {

        logger.info("GRAIN-API: Export PDF month={}", month);
        try {
            byte[] content = exportService.exportPdf(month, user);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=transactions-" + month + ".pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(content);
        } catch (DocumentException e) {
            throw new InternalServerError("Failed to generate PDF: " + e.getMessage());
        }
    }
}
