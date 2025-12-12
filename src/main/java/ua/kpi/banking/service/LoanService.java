package ua.kpi.banking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.kpi.banking.dto.loan.CreateLoanRequest;
import ua.kpi.banking.dto.loan.LoanResponse;
import ua.kpi.banking.dto.loan.UpdateLoanRequest;
import ua.kpi.banking.model.Account;
import ua.kpi.banking.model.Loan;
import ua.kpi.banking.model.LoanStatus;
import ua.kpi.banking.repository.AccountRepository;
import ua.kpi.banking.repository.LoanRepository;

import java.util.List;

@Service
public class LoanService {

    private final LoanRepository loanRepository;
    private final AccountRepository accountRepository;

    @Autowired
    public LoanService(LoanRepository loanRepository, AccountRepository accountRepository) {
        this.loanRepository = loanRepository;
        this.accountRepository = accountRepository;
    }

    public LoanResponse createLoan(CreateLoanRequest loan) {

        Account account = accountRepository.findById(loan.getAccountId())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        Loan newLoan = new Loan();

        newLoan.setAmount(loan.getAmount());
        newLoan.setEndDate(loan.getEndDate());
        newLoan.setStartDate(loan.getStartDate());
        newLoan.setStatus(LoanStatus.valueOf(loan.getStatus()));
        newLoan.setInterestRate(loan.getInterestRate());
        newLoan.setAccount(account);
        newLoan = loanRepository.save(newLoan);

        return toResponse(newLoan);
    }

    public LoanResponse getLoanById(Long id) {
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Loan not found") );
        return toResponse(loan);
    }

    public List<LoanResponse> getByAccountId(Long accountId) {
        return loanRepository.findByAccountId(accountId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<LoanResponse> getByStatus(LoanStatus status) {
        return loanRepository.findByStatus(status)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public LoanResponse updateLoan(Long id, UpdateLoanRequest loan) {
        Loan existingLoan = loanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Loan with id " + id + " not found"));

        existingLoan.setEndDate(existingLoan.getEndDate());
        existingLoan.setStatus(existingLoan.getStatus());
        return toResponse(loanRepository.save(existingLoan));
    }

    public void deleteLoan(Long id) {
        Loan existingLoan = loanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Loan with id " + id + " not found"));
        loanRepository.deleteById(id);
    }

    private LoanResponse toResponse(Loan loan) {
        return new LoanResponse(
                loan.getId(),
                loan.getAmount(),
                loan.getInterestRate(),
                loan.getStartDate(),
                loan.getEndDate(),
                loan.getStatus().name(),
                loan.getAccount().getId()
        );
    }
}
