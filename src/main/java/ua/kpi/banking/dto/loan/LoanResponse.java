package ua.kpi.banking.dto.loan;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ua.kpi.banking.model.LoanStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class LoanResponse {

    private Long id;
    private BigDecimal amount;
    private BigDecimal interestRate;
    private LocalDate startDate;
    private LocalDate endDate;
    private LoanStatus status;
    private Long accountId;
}
