package ua.kpi.banking.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ua.kpi.banking.dto.account.CreateAccountRequest;
import ua.kpi.banking.dto.customer.CreateCustomerRequest;
import ua.kpi.banking.dto.transaction.CreateTransactionRequest;
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

    @Test
    void shouldTransferMoneyBetweenAccounts() {

        CreateCustomerRequest customerRequest = new CreateCustomerRequest();
        customerRequest.setName("Ivan");
        customerRequest.setSurname("Petrenko");
        customerRequest.setEmail("ivan@test.com");
        customerRequest.setPhoneNumber("+380000000");

        var customer = customerService.createCustomer(customerRequest);

        CreateAccountRequest fromReq = new CreateAccountRequest();
        fromReq.setIban("UA111");
        fromReq.setBalance(BigDecimal.valueOf(1000));
        fromReq.setCurrency("UAH");
        fromReq.setType(AccountType.SAVING);
        fromReq.setCustomerId(customer.getId());

        var fromAccount = accountService.createAccount(fromReq);

        CreateAccountRequest toReq = new CreateAccountRequest();
        toReq.setIban("UA222");
        toReq.setBalance(BigDecimal.ZERO);
        toReq.setCurrency("UAH");
        toReq.setType(AccountType.SAVING);
        toReq.setCustomerId(customer.getId());

        var toAccount = accountService.createAccount(toReq);

        CreateTransactionRequest tx = new CreateTransactionRequest();
        tx.setFromAccountId(fromAccount.getId());
        tx.setToAccountId(toAccount.getId());
        tx.setAmount(BigDecimal.valueOf(300));
        tx.setType(TransactionType.TRANSFER);
        tx.setDescription("Test transfer");

        transactionService.createTransaction(tx);

        var updatedFrom = accountService.findById(fromAccount.getId());
        var updatedTo = accountService.findById(toAccount.getId());

        assertEquals(BigDecimal.valueOf(700), updatedFrom.getBalance());
        assertEquals(BigDecimal.valueOf(300), updatedTo.getBalance());
    }
}