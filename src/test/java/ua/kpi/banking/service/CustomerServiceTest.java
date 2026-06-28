package ua.kpi.banking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.kpi.banking.dto.customer.CreateCustomerRequest;
import ua.kpi.banking.dto.customer.CustomerResponse;
import ua.kpi.banking.dto.customer.UpdateCustomerRequest;
import ua.kpi.banking.exception.NotFoundException;
import ua.kpi.banking.model.*;
import ua.kpi.banking.repository.AccountRepository;
import ua.kpi.banking.repository.CardRepository;
import ua.kpi.banking.repository.CustomerRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CardRepository cardRepository;

    @InjectMocks
    private CustomerService customerService;

    private Customer activeCustomer;

    @BeforeEach
    void setUp() {
        activeCustomer = new Customer();
        activeCustomer.setId(1L);
        activeCustomer.setName("Olena");
        activeCustomer.setSurname("Kovalenko");
        activeCustomer.setEmail("olena@test.com");
        activeCustomer.setPhoneNumber("+380000001");
        activeCustomer.setStatus(CustomerStatus.ACTIVE);
    }

    @Test
    void shouldCreateCustomer() {
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> {
            Customer c = invocation.getArgument(0);
            c.setId(5L);
            return c;
        });

        CreateCustomerRequest req = new CreateCustomerRequest();
        req.setName("Ivan");
        req.setSurname("Petrenko");
        req.setEmail("ivan@test.com");
        req.setPhoneNumber("+380000002");

        CustomerResponse response = customerService.createCustomer(req);

        assertEquals(5L, response.getId());
        assertEquals("Ivan", response.getName());
        assertEquals("ivan@test.com", response.getEmail());
    }

    @Test
    void shouldFindCustomerById() {
        when(customerRepository.findByIdAndStatus(1L, CustomerStatus.ACTIVE))
                .thenReturn(Optional.of(activeCustomer));

        CustomerResponse response = customerService.findById(1L);

        assertEquals("Olena", response.getName());
        assertEquals("olena@test.com", response.getEmail());
    }

    @Test
    void shouldThrowWhenCustomerByIdNotFoundOrClosed() {
        when(customerRepository.findByIdAndStatus(404L, CustomerStatus.ACTIVE))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> customerService.findById(404L));
    }

    @Test
    void shouldFindCustomerByEmail() {
        when(customerRepository.findByEmailAndStatus("olena@test.com", CustomerStatus.ACTIVE))
                .thenReturn(Optional.of(activeCustomer));

        CustomerResponse response = customerService.findByEmail("olena@test.com");

        assertEquals(1L, response.getId());
    }

    @Test
    void shouldThrowWhenEmailNotFound() {
        when(customerRepository.findByEmailAndStatus("unknown@test.com", CustomerStatus.ACTIVE))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> customerService.findByEmail("unknown@test.com"));
    }

    @Test
    void shouldReturnAllActiveCustomers() {
        when(customerRepository.findAllByStatus(CustomerStatus.ACTIVE))
                .thenReturn(List.of(activeCustomer));

        List<CustomerResponse> result = customerService.findAll();

        assertEquals(1, result.size());
        assertEquals("Olena", result.get(0).getName());
    }

    @Test
    void shouldReturnEmptyListWhenNoActiveCustomers() {
        when(customerRepository.findAllByStatus(CustomerStatus.ACTIVE))
                .thenReturn(List.of());

        assertTrue(customerService.findAll().isEmpty());
    }

    @Test
    void shouldUpdateExistingCustomer() {
        when(customerRepository.findByIdAndStatus(1L, CustomerStatus.ACTIVE))
                .thenReturn(Optional.of(activeCustomer));
        when(customerRepository.save(any(Customer.class))).thenAnswer(inv -> inv.getArgument(0));

        UpdateCustomerRequest req = new UpdateCustomerRequest();
        req.setName("Olena-Updated");
        req.setSurname("Kovalenko");
        req.setEmail("new-email@test.com");
        req.setPhoneNumber("+380000099");

        CustomerResponse response = customerService.updateCustomer(1L, req);

        assertEquals("Olena-Updated", response.getName());
        assertEquals("new-email@test.com", response.getEmail());
        verify(customerRepository).save(activeCustomer);
    }

    @Test
    void shouldThrowWhenUpdatingMissingOrClosedCustomer() {
        when(customerRepository.findByIdAndStatus(404L, CustomerStatus.ACTIVE))
                .thenReturn(Optional.empty());

        UpdateCustomerRequest req = new UpdateCustomerRequest();

        assertThrows(NotFoundException.class, () -> customerService.updateCustomer(404L, req));
        verify(customerRepository, never()).save(any());
    }

    @Test
    void shouldCloseCustomerAndCascadeCloseAccountsAndCards() {
        Account account = new Account();
        account.setId(20L);
        account.setStatus(AccountStatus.ACTIVE);

        Card card = new Card();
        card.setId(30L);
        card.setStatus(CardStatus.ACTIVE);

        when(customerRepository.findByIdAndStatus(1L, CustomerStatus.ACTIVE))
                .thenReturn(Optional.of(activeCustomer));
        when(accountRepository.findByCustomer_IdAndStatus(1L, AccountStatus.ACTIVE))
                .thenReturn(List.of(account));
        when(cardRepository.findByAccountId(20L))
                .thenReturn(List.of(card));

        customerService.deleteCustomer(1L);

        assertEquals(CardStatus.CLOSED, card.getStatus());
        assertEquals(AccountStatus.CLOSED, account.getStatus());
        assertEquals(CustomerStatus.CLOSED, activeCustomer.getStatus());
        verify(cardRepository).saveAll(List.of(card));
        verify(customerRepository).save(activeCustomer);
    }

    @Test
    void shouldCloseCustomerWithNoAccountsWithoutError() {
        when(customerRepository.findByIdAndStatus(1L, CustomerStatus.ACTIVE))
                .thenReturn(Optional.of(activeCustomer));
        when(accountRepository.findByCustomer_IdAndStatus(1L, AccountStatus.ACTIVE))
                .thenReturn(List.of());

        customerService.deleteCustomer(1L);

        assertEquals(CustomerStatus.CLOSED, activeCustomer.getStatus());
        verify(cardRepository, never()).findByAccountId(any());
    }

    @Test
    void shouldThrowWhenDeletingMissingOrAlreadyClosedCustomer() {
        when(customerRepository.findByIdAndStatus(404L, CustomerStatus.ACTIVE))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> customerService.deleteCustomer(404L));
        verifyNoInteractions(accountRepository, cardRepository);
    }
}