package ua.kpi.banking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.kpi.banking.model.Loan;
import ua.kpi.banking.model.LoanStatus;

import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    // exclude closed
    List<Loan> findByAccountIdAndStatusNot(Long accountId, LoanStatus status);

    List<Loan> findByStatus(LoanStatus status);
}
