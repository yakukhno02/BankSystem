package ua.kpi.banking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.kpi.banking.dto.loan.CreateLoanRequest;
import ua.kpi.banking.dto.loan.LoanResponse;
import ua.kpi.banking.dto.loan.UpdateLoanRequest;
import ua.kpi.banking.exception.NotFoundException;
import ua.kpi.banking.model.Account;
import ua.kpi.banking.model.Loan;
import ua.kpi.banking.model.LoanStatus;
import ua.kpi.banking.repository.AccountRepository;
import ua.kpi.banking.repository.LoanRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanServiceTest {

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private LoanService loanService;

    private Account account;
    private Loan existingLoan;

    @BeforeEach
    void setUp() {
        account = new Account();
        account.setId(10L);

        existingLoan = new Loan();
        existingLoan.setId(1L);
        existingLoan.setAmount(BigDecimal.valueOf(10000));
        existingLoan.setInterestRate(BigDecimal.valueOf(5.5));
        existingLoan.setStartDate(LocalDate.now());
        existingLoan.setEndDate(LocalDate.now().plusYears(1));
        existingLoan.setStatus(LoanStatus.ACTIVE);
        existingLoan.setAccount(account);
    }

    @Test
    void shouldCreateLoanForExistingAccount() {
        when(accountRepository.findById(10L)).thenReturn(Optional.of(account));
        when(loanRepository.save(any(Loan.class))).thenAnswer(inv -> inv.getArgument(0));

        CreateLoanRequest req = new CreateLoanRequest();
        req.setAmount(BigDecimal.valueOf(5000));
        req.setInterestRate(BigDecimal.valueOf(3.5));
        req.setStartDate(LocalDate.now());
        req.setEndDate(LocalDate.now().plusMonths(6));
        req.setStatus(LoanStatus.ACTIVE);
        req.setAccountId(10L);

        LoanResponse response = loanService.createLoan(req);

        assertEquals(BigDecimal.valueOf(5000), response.getAmount());
        assertEquals(LoanStatus.ACTIVE, response.getStatus());
        verify(loanRepository).save(any(Loan.class));
    }

    @Test
    void shouldThrowWhenCreatingLoanForMissingAccount() {
        when(accountRepository.findById(99L)).thenReturn(Optional.empty());

        CreateLoanRequest req = new CreateLoanRequest();
        req.setAccountId(99L);

        assertThrows(NotFoundException.class, () -> loanService.createLoan(req));
        verifyNoInteractions(loanRepository);
    }

    @Test
    void shouldGetLoanById() {
        when(loanRepository.findById(1L)).thenReturn(Optional.of(existingLoan));

        LoanResponse response = loanService.getLoanById(1L);

        assertEquals(BigDecimal.valueOf(10000), response.getAmount());
    }

    @Test
    void shouldThrowWhenLoanByIdNotFound() {
        when(loanRepository.findById(404L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> loanService.getLoanById(404L));
    }

    @Test
    void shouldGetActiveAndOverdueLoansByAccountId() {
        Loan overdueLoan = new Loan();
        overdueLoan.setId(2L);
        overdueLoan.setStatus(LoanStatus.OVERDUE);
        overdueLoan.setAccount(account);

        when(accountRepository.findById(10L)).thenReturn(Optional.of(account));
        when(loanRepository.findByAccountIdAndStatusNot(10L, LoanStatus.CLOSED))
                .thenReturn(List.of(existingLoan, overdueLoan));

        List<LoanResponse> result = loanService.getByAccountId(10L);

        assertEquals(2, result.size());
    }

    @Test
    void shouldExcludeClosedLoansFromAccountListing() {
        when(accountRepository.findById(10L)).thenReturn(Optional.of(account));
        when(loanRepository.findByAccountIdAndStatusNot(10L, LoanStatus.CLOSED))
                .thenReturn(List.of(existingLoan)); // closed loan never returned

        List<LoanResponse> result = loanService.getByAccountId(10L);

        assertEquals(1, result.size());
        assertNotEquals(LoanStatus.CLOSED, result.get(0).getStatus());
        verify(loanRepository).findByAccountIdAndStatusNot(10L, LoanStatus.CLOSED);
    }

    @Test
    void shouldThrowWhenGettingLoansForMissingAccount() {
        when(accountRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> loanService.getByAccountId(99L));
        verifyNoInteractions(loanRepository);
    }

    @Test
    void shouldGetLoansByStatus() {
        when(loanRepository.findByStatus(LoanStatus.ACTIVE)).thenReturn(List.of(existingLoan));

        List<LoanResponse> result = loanService.getByStatus(LoanStatus.ACTIVE);

        assertEquals(1, result.size());
    }

    @Test
    void shouldGetClosedLoansExplicitlyByStatus() {
        existingLoan.setStatus(LoanStatus.CLOSED);
        when(loanRepository.findByStatus(LoanStatus.CLOSED)).thenReturn(List.of(existingLoan));

        List<LoanResponse> result = loanService.getByStatus(LoanStatus.CLOSED);

        assertEquals(1, result.size());
        assertEquals(LoanStatus.CLOSED, result.get(0).getStatus());
    }

    @Test
    void shouldReturnEmptyListWhenNoLoansMatchStatus() {
        when(loanRepository.findByStatus(LoanStatus.OVERDUE)).thenReturn(List.of());

        assertTrue(loanService.getByStatus(LoanStatus.OVERDUE).isEmpty());
    }

    @Test
    void shouldUpdateExistingLoan() {
        when(loanRepository.findById(1L)).thenReturn(Optional.of(existingLoan));
        when(loanRepository.save(any(Loan.class))).thenAnswer(inv -> inv.getArgument(0));

        UpdateLoanRequest req = new UpdateLoanRequest();
        req.setEndDate(LocalDate.now().plusYears(2));
        req.setStatus(LoanStatus.OVERDUE);

        LoanResponse response = loanService.updateLoan(1L, req);

        assertEquals(LoanStatus.OVERDUE, response.getStatus());
        assertEquals(LocalDate.now().plusYears(2), response.getEndDate());
    }

    @Test
    void shouldThrowWhenUpdatingMissingLoan() {
        when(loanRepository.findById(404L)).thenReturn(Optional.empty());

        UpdateLoanRequest req = new UpdateLoanRequest();

        assertThrows(NotFoundException.class, () -> loanService.updateLoan(404L, req));
        verify(loanRepository, never()).save(any());
    }

    @Test
    void shouldSoftDeleteLoanByClosingIt() {
        when(loanRepository.findById(1L)).thenReturn(Optional.of(existingLoan));
        when(loanRepository.save(any(Loan.class))).thenAnswer(inv -> inv.getArgument(0));

        loanService.deleteLoan(1L);

        assertEquals(LoanStatus.CLOSED, existingLoan.getStatus());
        verify(loanRepository).save(existingLoan);
    }

    @Test
    void shouldThrowWhenDeletingMissingLoan() {
        when(loanRepository.findById(404L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> loanService.deleteLoan(404L));
        verify(loanRepository, never()).save(any());
    }
}