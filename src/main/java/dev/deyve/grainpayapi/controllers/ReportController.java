package dev.deyve.grainpayapi.controllers;

import dev.deyve.grainpayapi.dtos.Response;
import dev.deyve.grainpayapi.models.User;
import dev.deyve.grainpayapi.services.ReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Year;
import java.time.YearMonth;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private static final Logger logger = LoggerFactory.getLogger(ReportController.class);

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/monthly")
    public ResponseEntity<Response> monthly(
            @RequestParam(defaultValue = "#{T(java.time.Year).now().getValue()}") Integer year,
            @AuthenticationPrincipal User user) {

        logger.info("GRAIN-API: Monthly report year={}", year);
        return new ResponseEntity<>(
                new Response(reportService.getMonthlyReport(year, user), OK.value(), "Monthly report"), OK);
    }

    @GetMapping("/yearly")
    public ResponseEntity<Response> yearly(@AuthenticationPrincipal User user) {
        logger.info("GRAIN-API: Yearly report");
        return new ResponseEntity<>(
                new Response(reportService.getYearlyReport(user), OK.value(), "Yearly report"), OK);
    }

    @GetMapping("/by-category")
    public ResponseEntity<Response> byCategory(
            @RequestParam YearMonth month,
            @AuthenticationPrincipal User user) {

        logger.info("GRAIN-API: Category report month={}", month);
        return new ResponseEntity<>(
                new Response(reportService.getCategoryReport(month, user), OK.value(), "Category report"), OK);
    }
}
