package ua.kpi.banking.dto.loan;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import ua.kpi.banking.model.LoanStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class CreateLoanRequest {

    @NotNull
    @Positive
    private BigDecimal amount;

    @NotNull
    @Positive
    private BigDecimal interestRate;

    @NotNull
    private LocalDate startDate;

    @NotNull
    @Future
    private LocalDate endDate;

    @NotNull
    private LoanStatus status;

    @NotNull
    private Long accountId;
}
