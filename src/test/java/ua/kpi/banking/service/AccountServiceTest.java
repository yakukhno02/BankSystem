package ua.kpi.banking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.kpi.banking.dto.account.AccountResponse;
import ua.kpi.banking.dto.account.CreateAccountRequest;
import ua.kpi.banking.exception.NotFoundException;
import ua.kpi.banking.model.*;
import ua.kpi.banking.repository.AccountRepository;
import ua.kpi.banking.repository.CardRepository;
import ua.kpi.banking.repository.CustomerRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CardRepository cardRepository;

    @InjectMocks
    private AccountService accountService;

    private Customer activeCustomer;
    private Account activeAccount;

    @BeforeEach
    void setUp() {
        activeCustomer = new Customer();
        activeCustomer.setId(1L);
        activeCustomer.setStatus(CustomerStatus.ACTIVE);

        activeAccount = new Account();
        activeAccount.setId(10L);
        activeAccount.setIban("UA123456");
        activeAccount.setBalance(BigDecimal.valueOf(1000));
        activeAccount.setCurrency("UAH");
        activeAccount.setType(AccountType.SAVING);
        activeAccount.setStatus(AccountStatus.ACTIVE);
        activeAccount.setCustomer(activeCustomer);
    }

    @Test
    void shouldCreateAccountForActiveCustomer() {
        when(customerRepository.findByIdAndStatus(1L, CustomerStatus.ACTIVE))
                .thenReturn(Optional.of(activeCustomer));

        CreateAccountRequest req = new CreateAccountRequest();
        req.setIban("UA999999");
        req.setBalance(BigDecimal.valueOf(500));
        req.setCurrency("UAH");
        req.setType(AccountType.SAVING);
        req.setCustomerId(1L);

        AccountResponse response = accountService.createAccount(req);

        assertEquals("UA999999", response.getIban());
        assertEquals(BigDecimal.valueOf(500), response.getBalance());
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    void shouldThrowWhenCreatingAccountForMissingOrInactiveCustomer() {
        when(customerRepository.findByIdAndStatus(99L, CustomerStatus.ACTIVE))
                .thenReturn(Optional.empty());

        CreateAccountRequest req = new CreateAccountRequest();
        req.setCustomerId(99L);

        assertThrows(NotFoundException.class, () -> accountService.createAccount(req));
        verifyNoInteractions(accountRepository);
    }

    @Test
    void shouldFindAccountById() {
        when(accountRepository.findByIdAndStatus(10L, AccountStatus.ACTIVE))
                .thenReturn(Optional.of(activeAccount));

        AccountResponse response = accountService.findById(10L);

        assertEquals(10L, response.getId());
        assertEquals("UA123456", response.getIban());
    }

    @Test
    void shouldThrowWhenAccountByIdNotFoundOrClosed() {
        when(accountRepository.findByIdAndStatus(404L, AccountStatus.ACTIVE))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> accountService.findById(404L));
    }

    @Test
    void shouldFindAccountByIban() {
        when(accountRepository.findByIbanAndStatus("UA123456", AccountStatus.ACTIVE))
                .thenReturn(Optional.of(activeAccount));

        AccountResponse response = accountService.findByIban("UA123456");

        assertEquals("UA123456", response.getIban());
    }

    @Test
    void shouldThrowWhenIbanNotFound() {
        when(accountRepository.findByIbanAndStatus("UNKNOWN", AccountStatus.ACTIVE))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> accountService.findByIban("UNKNOWN"));
    }

    @Test
    void shouldFindAccountsByCustomerId() {
        when(customerRepository.findByIdAndStatus(1L, CustomerStatus.ACTIVE))
                .thenReturn(Optional.of(activeCustomer));
        when(accountRepository.findByCustomer_IdAndStatus(1L, AccountStatus.ACTIVE))
                .thenReturn(List.of(activeAccount));

        List<AccountResponse> result = accountService.findByCustomerId(1L);

        assertEquals(1, result.size());
        assertEquals("UA123456", result.get(0).getIban());
    }

    @Test
    void shouldThrowWhenFindingAccountsForMissingCustomer() {
        when(customerRepository.findByIdAndStatus(99L, CustomerStatus.ACTIVE))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> accountService.findByCustomerId(99L));
        verifyNoInteractions(accountRepository);
    }

    @Test
    void shouldReturnAllActiveAccounts() {
        when(accountRepository.findAllByStatus(AccountStatus.ACTIVE))
                .thenReturn(List.of(activeAccount));

        List<AccountResponse> result = accountService.findAll();

        assertEquals(1, result.size());
    }

    @Test
    void shouldReturnEmptyListWhenNoActiveAccounts() {
        when(accountRepository.findAllByStatus(AccountStatus.ACTIVE))
                .thenReturn(List.of());

        List<AccountResponse> result = accountService.findAll();

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldCloseAccountAndAllItsCards() {
        Card card1 = new Card();
        card1.setId(1L);
        card1.setStatus(CardStatus.ACTIVE);

        Card card2 = new Card();
        card2.setId(2L);
        card2.setStatus(CardStatus.ACTIVE);

        when(accountRepository.findByIdAndStatus(10L, AccountStatus.ACTIVE))
                .thenReturn(Optional.of(activeAccount));
        when(cardRepository.findByAccountId(10L))
                .thenReturn(List.of(card1, card2));

        accountService.deleteAccount(10L);

        assertEquals(CardStatus.CLOSED, card1.getStatus());
        assertEquals(CardStatus.CLOSED, card2.getStatus());
        assertEquals(AccountStatus.CLOSED, activeAccount.getStatus());
        verify(cardRepository).saveAll(List.of(card1, card2));
        verify(accountRepository).save(activeAccount);
    }

    @Test
    void shouldCloseAccountWithNoCardsWithoutError() {
        when(accountRepository.findByIdAndStatus(10L, AccountStatus.ACTIVE))
                .thenReturn(Optional.of(activeAccount));
        when(cardRepository.findByAccountId(10L))
                .thenReturn(List.of());

        accountService.deleteAccount(10L);

        assertEquals(AccountStatus.CLOSED, activeAccount.getStatus());
        verify(cardRepository).saveAll(List.of());
    }

    @Test
    void shouldThrowWhenDeletingMissingOrAlreadyClosedAccount() {
        when(accountRepository.findByIdAndStatus(404L, AccountStatus.ACTIVE))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> accountService.deleteAccount(404L));
        verifyNoInteractions(cardRepository);
    }
}