package ua.kpi.banking.controller;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.kpi.banking.dto.loan.CreateLoanRequest;
import ua.kpi.banking.dto.loan.LoanResponse;
import ua.kpi.banking.dto.loan.UpdateLoanRequest;
import ua.kpi.banking.model.LoanStatus;
import ua.kpi.banking.service.LoanService;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @PostMapping
    public ResponseEntity<LoanResponse> createLoan(@Valid @RequestBody CreateLoanRequest loan) {
        return ResponseEntity.ok(loanService.createLoan(loan));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LoanResponse> getLoanById(@PathVariable Long id) {
        return ResponseEntity.ok(loanService.getLoanById(id));
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<LoanResponse>> getLoanByAccountId(@PathVariable Long accountId) {
        return ResponseEntity.ok(loanService.getByAccountId(accountId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<LoanResponse>> getLoanByStatus(@PathVariable @Schema(implementation = LoanStatus.class) LoanStatus status) {
        return ResponseEntity.ok(loanService.getByStatus(status));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LoanResponse> updateLoan(@PathVariable Long id, @Valid @RequestBody UpdateLoanRequest loan) {
        return ResponseEntity.ok(loanService.updateLoan(id, loan));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLoan(@PathVariable Long id) {
        loanService.deleteLoan(id);
        return ResponseEntity.noContent().build();
    }
}
