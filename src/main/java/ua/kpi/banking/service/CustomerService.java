package ua.kpi.banking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.kpi.banking.dto.customer.CreateCustomerRequest;
import ua.kpi.banking.dto.customer.CustomerResponse;
import ua.kpi.banking.dto.customer.UpdateCustomerRequest;
import ua.kpi.banking.exception.NotFoundException;
import ua.kpi.banking.model.Account;
import ua.kpi.banking.model.Card;
import ua.kpi.banking.model.CardStatus;
import ua.kpi.banking.model.Customer;
import ua.kpi.banking.repository.AccountRepository;
import ua.kpi.banking.repository.CardRepository;
import ua.kpi.banking.repository.CustomerRepository;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;
    private final CardRepository cardRepository;

    @Autowired
    public CustomerService(CustomerRepository customerRepository, AccountRepository accountRepository, CardRepository cardRepository) {
        this.customerRepository = customerRepository;
        this.accountRepository = accountRepository;
        this.cardRepository = cardRepository;
    }

    public CustomerResponse createCustomer(CreateCustomerRequest request) {
        Customer customer = new Customer();
        customer.setName(request.getName());
        customer.setSurname(request.getSurname());
        customer.setEmail(request.getEmail());
        customer.setPhoneNumber(request.getPhoneNumber());

        return toResponse(customerRepository.save(customer));
    }

    public CustomerResponse findById(Long id) {
        Customer customer = customerRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException("Customer not found"));

        return toResponse(customer);
    }

    public CustomerResponse findByEmail(String email) {
        Customer customer = customerRepository.findByEmailAndIsDeletedFalse(email)
                .orElseThrow(() -> new NotFoundException("Customer not found"));

        return toResponse(customer);
    }

    public List<CustomerResponse> findAll() {
       return customerRepository.findAllByIsDeletedFalse()
               .stream()
               .map(this::toResponse)
               .toList();
    }

    public CustomerResponse updateCustomer(Long id, UpdateCustomerRequest customer) {
        Customer existingCustomer = customerRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException("Customer with id " + id + " not found"));

        existingCustomer.setName(customer.getName());
        existingCustomer.setSurname(customer.getSurname());
        existingCustomer.setEmail(customer.getEmail());
        existingCustomer.setPhoneNumber(customer.getPhoneNumber());

        return toResponse(customerRepository.save(existingCustomer));
    }

    @Transactional
    public void deleteCustomer(Long id) {
        Customer existingCustomer = customerRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException("Customer with id " + id + " not found"));

        List<Account> accounts = accountRepository.findByCustomerIdAndIsDeletedFalse(id);

        for (Account account : accounts) {
            List<Card> cards = cardRepository.findByAccountId(account.getId());
            for (Card card : cards) {
                card.setStatus(CardStatus.CLOSED);
            }
            cardRepository.saveAll(cards);
            account.setDeleted(true);
        }

        existingCustomer.setDeleted(true);
        customerRepository.save(existingCustomer);
    }

    private CustomerResponse toResponse(Customer customer) {
        return new CustomerResponse(
                customer.getId(),
                customer.getName(),
                customer.getSurname(),
                customer.getEmail(),
                customer.getPhoneNumber()
        );
    }
}
