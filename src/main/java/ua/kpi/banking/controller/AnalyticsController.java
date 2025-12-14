package ua.kpi.banking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.kpi.banking.dto.analytics.CustomerTransactionStatsResponse;
import ua.kpi.banking.service.AnalyticsService;

import java.util.List;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/customers/transactions")
    public ResponseEntity<List<CustomerTransactionStatsResponse>> getCustomerTransactionStats() {
        return ResponseEntity.ok(analyticsService.getCustomerTransactionStats());
    }
}