package ua.kpi.banking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.kpi.banking.dto.analytics.CustomerTransactionStats;
import ua.kpi.banking.repository.AnalyticsRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final AnalyticsRepository analyticsRepository;

    public List<CustomerTransactionStats> getCustomerTransactionStats() {
        return analyticsRepository.getCustomerTransactionStats();
    }
}