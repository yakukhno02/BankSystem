package ua.kpi.banking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import ua.kpi.banking.dto.transaction.CreateTransactionRequest;
import ua.kpi.banking.exception.BadRequestException;
import ua.kpi.banking.exception.NotFoundException;
import ua.kpi.banking.model.Account;
import ua.kpi.banking.model.AccountType;
import ua.kpi.banking.model.TransactionType;
import ua.kpi.banking.repository.AccountRepository;
import ua.kpi.banking.repository.TransactionRepository;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private TransactionService transactionService;

    private Account savingFrom;
    private Account savingTo;
    private Account depositAccount;

    @BeforeEach
    void setUp() {
        savingFrom = new Account();
        savingFrom.setId(1L);
        savingFrom.setBalance(BigDecimal.valueOf(1000));
        savingFrom.setCurrency("UAH");
        savingFrom.setType(AccountType.SAVING);

        savingTo = new Account();
        savingTo.setId(2L);
        savingTo.setBalance(BigDecimal.valueOf(500));
        savingTo.setCurrency("UAH");
        savingTo.setType(AccountType.SAVING);

        depositAccount = new Account();
        depositAccount.setId(3L);
        depositAccount.setBalance(BigDecimal.valueOf(2000));
        depositAccount.setCurrency("UAH");
        depositAccount.setType(AccountType.DEPOSIT);
    }

    private CreateTransactionRequest request(TransactionType type, BigDecimal amount, Long from, Long to) {
        CreateTransactionRequest req = new CreateTransactionRequest();
        req.setType(type);
        req.setAmount(amount);
        req.setFromAccountId(from);
        req.setToAccountId(to);
        return req;
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "-5"})
    void shouldRejectNonPositiveAmount(String amount) {
        var req = request(TransactionType.WITHDRAW, new BigDecimal(amount), 1L, null);

        assertThrows(BadRequestException.class, () -> transactionService.createTransaction(req));
        verifyNoInteractions(accountRepository);
    }

    @Test
    void shouldRejectNullAmount() {
        var req = request(TransactionType.WITHDRAW, null, 1L, null);

        assertThrows(BadRequestException.class, () -> transactionService.createTransaction(req));
    }

    @Test
    void shouldRejectNullType() {
        var req = request(null, BigDecimal.TEN, 1L, null);

        assertThrows(BadRequestException.class, () -> transactionService.createTransaction(req));
    }

    @Test
    void shouldTransferBetweenAccounts() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(savingFrom));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(savingTo));

        var req = request(TransactionType.TRANSFER, BigDecimal.valueOf(300), 1L, 2L);
        transactionService.createTransaction(req);

        assertEquals(BigDecimal.valueOf(700), savingFrom.getBalance());
        assertEquals(BigDecimal.valueOf(800), savingTo.getBalance());
        verify(transactionRepository).save(any());
    }

    @Test
    void shouldRejectTransferFromDepositAccount() {
        when(accountRepository.findById(3L)).thenReturn(Optional.of(depositAccount));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(savingTo));

        var req = request(TransactionType.TRANSFER, BigDecimal.valueOf(100), 3L, 2L);

        assertThrows(BadRequestException.class, () -> transactionService.createTransaction(req));
        assertEquals(BigDecimal.valueOf(2000), depositAccount.getBalance(), "balance must stay unchanged");
    }

    @Test
    void shouldRejectTransferWithMismatchedCurrency() {
        savingTo.setCurrency("USD");
        when(accountRepository.findById(1L)).thenReturn(Optional.of(savingFrom));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(savingTo));

        var req = request(TransactionType.TRANSFER, BigDecimal.valueOf(100), 1L, 2L);

        assertThrows(BadRequestException.class, () -> transactionService.createTransaction(req));
    }

    @Test
    void shouldRejectTransferWithInsufficientFunds() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(savingFrom));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(savingTo));

        var req = request(TransactionType.TRANSFER, BigDecimal.valueOf(5000), 1L, 2L);

        assertThrows(BadRequestException.class, () -> transactionService.createTransaction(req));
        assertEquals(BigDecimal.valueOf(1000), savingFrom.getBalance());
    }

    @Test
    void shouldThrowWhenFromAccountNotFoundOnTransfer() {
        when(accountRepository.findById(99L)).thenReturn(Optional.empty());

        var req = request(TransactionType.TRANSFER, BigDecimal.valueOf(100), 99L, 2L);

        assertThrows(NotFoundException.class, () -> transactionService.createTransaction(req));
    }


    @Test
    void shouldWithdrawFromSavingAccount() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(savingFrom));

        var req = request(TransactionType.WITHDRAW, BigDecimal.valueOf(400), 1L, null);
        transactionService.createTransaction(req);

        assertEquals(BigDecimal.valueOf(600), savingFrom.getBalance());
    }

    @Test
    void shouldRejectWithdrawFromDepositAccountRegardlessOfBalance() {
        when(accountRepository.findById(3L)).thenReturn(Optional.of(depositAccount));

        var req = request(TransactionType.WITHDRAW, BigDecimal.valueOf(999999), 3L, null);

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> transactionService.createTransaction(req));
        assertTrue(ex.getMessage().toLowerCase().contains("deposit"),
                "should fail on account-type check before the funds check");
    }

    @Test
    void shouldRejectWithdrawWithInsufficientFunds() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(savingFrom));

        var req = request(TransactionType.WITHDRAW, BigDecimal.valueOf(5000), 1L, null);

        assertThrows(BadRequestException.class, () -> transactionService.createTransaction(req));
        assertEquals(BigDecimal.valueOf(1000), savingFrom.getBalance());
    }

    @Test
    void shouldThrowWhenAccountNotFoundOnWithdraw() {
        when(accountRepository.findById(42L)).thenReturn(Optional.empty());

        var req = request(TransactionType.WITHDRAW, BigDecimal.valueOf(100), 42L, null);

        assertThrows(NotFoundException.class, () -> transactionService.createTransaction(req));
    }

    @Test
    void shouldDepositIntoAccount() {
        when(accountRepository.findById(2L)).thenReturn(Optional.of(savingTo));

        var req = request(TransactionType.DEPOSIT, BigDecimal.valueOf(150), null, 2L);
        transactionService.createTransaction(req);

        assertEquals(BigDecimal.valueOf(650), savingTo.getBalance());
    }

    @Test
    void shouldThrowWhenAccountNotFoundOnDeposit() {
        when(accountRepository.findById(77L)).thenReturn(Optional.empty());

        var req = request(TransactionType.DEPOSIT, BigDecimal.valueOf(100), null, 77L);

        assertThrows(NotFoundException.class, () -> transactionService.createTransaction(req));
    }

    @Test
    void shouldThrowWhenGettingNonExistentTransaction() {
        when(transactionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> transactionService.getTransactionById(1L));
    }
}