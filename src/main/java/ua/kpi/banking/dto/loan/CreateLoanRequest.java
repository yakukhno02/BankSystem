package ua.kpi.banking.dto.loan;

import lombok.Getter;
import lombok.Setter;
import ua.kpi.banking.model.LoanStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class CreateLoanRequest {

    private BigDecimal amount;
    private BigDecimal interestRate;
    private LocalDate startDate;
    private LocalDate endDate;
    private LoanStatus status;
    private Long accountId;
}
