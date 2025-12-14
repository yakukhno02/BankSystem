package ua.kpi.banking.dto.analytics;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class CustomerTransactionStatsResponse {

    private Long customerId;
    private String name;
    private String surname;

    private Long transactionCount;
    private BigDecimal totalAmount;
    private BigDecimal avgTransactionAmount;
    private BigDecimal maxTransactionAmount;
}