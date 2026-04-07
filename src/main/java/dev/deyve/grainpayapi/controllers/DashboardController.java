package dev.deyve.grainpayapi.controllers;

import dev.deyve.grainpayapi.dtos.DashboardSummaryResponse;
import dev.deyve.grainpayapi.dtos.Response;
import dev.deyve.grainpayapi.models.User;
import dev.deyve.grainpayapi.services.DashboardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.YearMonth;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/summary")
    public ResponseEntity<Response> getSummary(
            @RequestParam YearMonth month,
            @AuthenticationPrincipal User user) {

        logger.info("GRAIN-API: Get dashboard summary month={}", month);
        DashboardSummaryResponse summary = dashboardService.getSummary(month, user);
        return new ResponseEntity<>(new Response(summary, OK.value(), "Dashboard summary"), OK);
    }
}
