package ua.kpi.banking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.kpi.banking.dto.analytics.CustomerTransactionStatsResponse;
import ua.kpi.banking.repository.AnalyticsRepository;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final AnalyticsRepository analyticsRepository;

    public List<CustomerTransactionStatsResponse> getCustomerTransactionStats() {
        return analyticsRepository.getCustomerTransactionStats()
                .stream()
                .map(row -> new CustomerTransactionStatsResponse(
                        ((Number) row[0]).longValue(),        // customerId
                        (String) row[1],                      // name
                        (String) row[2],                      // surname
                        ((Number) row[3]).longValue(),        // transactionCount
                        (BigDecimal) row[4],                  // totalAmount
                        (BigDecimal) row[5],                  // avgTransactionAmount
                        (BigDecimal) row[6]                   // maxTransactionAmount
                ))
                .toList();
    }
}