package ua.kpi.banking.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import ua.kpi.banking.dto.customer.CreateCustomerRequest;
import ua.kpi.banking.dto.customer.CustomerResponse;
import ua.kpi.banking.dto.account.CreateAccountRequest;
import ua.kpi.banking.dto.account.AccountResponse;
import ua.kpi.banking.model.AccountType;
import ua.kpi.banking.service.AccountService;
import ua.kpi.banking.service.CustomerService;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CustomerAccountIntegrationTest {

    @Autowired
    CustomerService customerService;

    @Autowired
    AccountService accountService;

    @Test
    void shouldCreateCustomerAndAccount() {

        CreateCustomerRequest customerRequest = new CreateCustomerRequest();
        customerRequest.setName("Ivan");
        customerRequest.setSurname("Petrenko");
        customerRequest.setEmail("ivan@test.com");
        customerRequest.setPhoneNumber("+380991112233");

        CustomerResponse customer = customerService.createCustomer(customerRequest);

        CreateAccountRequest accountRequest = new CreateAccountRequest();
        accountRequest.setIban("UA12345678901234567890123456");
        accountRequest.setBalance(BigDecimal.valueOf(1000));
        accountRequest.setCurrency("UAH");
        accountRequest.setType(AccountType.DEBIT);
        accountRequest.setCustomerId(customer.getId());

        AccountResponse account = accountService.createAccount(accountRequest);

        assertNotNull(customer.getId());
        assertNotNull(account.getId());
        assertEquals(customer.getId(), account.getCustomerId());
        assertEquals(BigDecimal.valueOf(1000), account.getBalance());
    }
}