package ua.kpi.banking.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ua.kpi.banking.dto.account.CreateAccountRequest;
import ua.kpi.banking.dto.customer.CreateCustomerRequest;
import ua.kpi.banking.dto.transaction.CreateTransactionRequest;
import ua.kpi.banking.exception.BadRequestException;
import ua.kpi.banking.model.AccountType;
import ua.kpi.banking.model.TransactionType;
import ua.kpi.banking.service.AccountService;
import ua.kpi.banking.service.CustomerService;
import ua.kpi.banking.service.TransactionService;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class TransactionIntegrationTest {

    @Autowired
    CustomerService customerService;

    @Autowired
    AccountService accountService;

    @Autowired
    TransactionService transactionService;

    private Long createCustomer() {
        CreateCustomerRequest req = new CreateCustomerRequest();
        req.setName("Olena");
        req.setSurname("Kovalenko");
        req.setEmail("olena" + System.nanoTime() + "@test.com");
        req.setPhoneNumber("+380000001");
        return customerService.createCustomer(req).getId();
    }

    private Long createAccount(Long customerId, String iban, BigDecimal balance, AccountType type) {
        CreateAccountRequest req = new CreateAccountRequest();
        req.setIban(iban);
        req.setBalance(balance);
        req.setCurrency("UAH");
        req.setType(type);
        req.setCustomerId(customerId);
        return accountService.createAccount(req).getId();
    }

    @Test
    void shouldDepositAndPersistNewBalance() {
        Long customerId = createCustomer();
        Long accountId = createAccount(customerId, "UA_DEP_1", BigDecimal.valueOf(100), AccountType.SAVING);

        CreateTransactionRequest tx = new CreateTransactionRequest();
        tx.setType(TransactionType.DEPOSIT);
        tx.setAmount(BigDecimal.valueOf(250));
        tx.setToAccountId(accountId);

        transactionService.createTransaction(tx);

        assertEquals(BigDecimal.valueOf(350), accountService.findById(accountId).getBalance());
    }

    @Test
    void shouldWithdrawAndPersistNewBalance() {
        Long customerId = createCustomer();
        Long accountId = createAccount(customerId, "UA_WD_1", BigDecimal.valueOf(500), AccountType.SAVING);

        CreateTransactionRequest tx = new CreateTransactionRequest();
        tx.setType(TransactionType.WITHDRAW);
        tx.setAmount(BigDecimal.valueOf(200));
        tx.setFromAccountId(accountId);

        transactionService.createTransaction(tx);

        assertEquals(BigDecimal.valueOf(300), accountService.findById(accountId).getBalance());
    }

    @Test
    void shouldRollBackBothBalancesWhenTransferFails() {
        Long customerId = createCustomer();
        Long fromId = createAccount(customerId, "UA_RB_FROM", BigDecimal.valueOf(1000), AccountType.SAVING);
        Long toId = createAccount(customerId, "UA_RB_TO", BigDecimal.valueOf(0), AccountType.SAVING);

        CreateTransactionRequest tx = new CreateTransactionRequest();
        tx.setType(TransactionType.TRANSFER);
        tx.setFromAccountId(fromId);
        tx.setToAccountId(toId);
        tx.setAmount(BigDecimal.valueOf(1500)); // exceeds balance -> should throw and not save anything

        assertThrows(BadRequestException.class, () -> transactionService.createTransaction(tx));

        // balances must be untouched since the whole operation is @Transactional
        assertEquals(BigDecimal.valueOf(1000), accountService.findById(fromId).getBalance());
        assertEquals(BigDecimal.valueOf(0), accountService.findById(toId).getBalance());
    }

    @Test
    void shouldRejectWithdrawFromDepositAccountEvenWithEnoughBalance() {
        Long customerId = createCustomer();
        Long accountId = createAccount(customerId, "UA_DEP_ACC", BigDecimal.valueOf(5000), AccountType.DEPOSIT);

        CreateTransactionRequest tx = new CreateTransactionRequest();
        tx.setType(TransactionType.WITHDRAW);
        tx.setFromAccountId(accountId);
        tx.setAmount(BigDecimal.valueOf(100)); // well within balance, but wrong account type

        assertThrows(BadRequestException.class, () -> transactionService.createTransaction(tx));
        assertEquals(BigDecimal.valueOf(5000), accountService.findById(accountId).getBalance());
    }
}