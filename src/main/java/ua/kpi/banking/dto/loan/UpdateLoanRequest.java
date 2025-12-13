package ua.kpi.banking.dto.loan;

import lombok.Getter;
import lombok.Setter;
import ua.kpi.banking.model.LoanStatus;

import java.time.LocalDate;

@Getter
@Setter
public class UpdateLoanRequest {
    private LocalDate endDate;
    private LoanStatus status;
}
