package ua.kpi.banking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.kpi.banking.model.Loan;
import ua.kpi.banking.model.LoanStatus;

import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    List<Loan> findByAccountId(Long accountId);

    List<Loan> findByStatus(LoanStatus status);
}
