package ua.kpi.banking.dto.loan;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import ua.kpi.banking.model.LoanStatus;

import java.time.LocalDate;

@Getter
@Setter
public class UpdateLoanRequest {


    @NotNull
    @Future
    private LocalDate endDate;

    @NotNull
    private LoanStatus status;
}
