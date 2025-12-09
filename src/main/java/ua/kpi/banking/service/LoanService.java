package ua.kpi.banking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.kpi.banking.model.Loan;
import ua.kpi.banking.model.LoanStatus;
import ua.kpi.banking.repository.LoanRepository;

import java.util.List;
import java.util.Optional;

@Service
public class LoanService {

    private final LoanRepository loanRepository;

    @Autowired
    public LoanService(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
    }

    public Loan createLoan(Loan loan) {
        return loanRepository.save(loan);
    }

    public Optional<Loan> getLoanById(Long id) {
        return loanRepository.findById(id);
    }

    public List<Loan> getByAccountId(Long accountId) {
        return loanRepository.findByAccountId(accountId);
    }

    public List<Loan> getByStatus(LoanStatus status) {
        return loanRepository.findByStatus(status);
    }

    public Loan updateLoan(Long id, Loan loan) {
        Loan existingLoan = loanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Loan with id " + id + " not found"));

        existingLoan.setEndDate(loan.getEndDate());
        existingLoan.setStatus(loan.getStatus());
        return loanRepository.save(existingLoan);
    }

    public void deleteLoan(Long id) {
        Loan existingLoan = loanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Loan with id " + id + " not found"));
        loanRepository.deleteById(id);
    }
}
