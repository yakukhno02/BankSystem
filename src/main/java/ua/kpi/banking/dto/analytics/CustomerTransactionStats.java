package ua.kpi.banking.dto.analytics;

import java.math.BigDecimal;

public interface CustomerTransactionStats {

    Long getCustomerId();
    String getName();
    String getSurname();

    Long getTransactionCount();
    BigDecimal getTotalAmount();
    BigDecimal getAvgTransactionAmount();
    BigDecimal getMaxTransactionAmount();
}
