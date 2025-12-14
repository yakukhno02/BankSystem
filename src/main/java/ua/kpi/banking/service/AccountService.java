package ua.kpi.banking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.kpi.banking.dto.account.AccountResponse;
import ua.kpi.banking.dto.account.CreateAccountRequest;
import ua.kpi.banking.exception.NotFoundException;
import ua.kpi.banking.model.Account;
import ua.kpi.banking.model.Customer;
import ua.kpi.banking.repository.AccountRepository;
import ua.kpi.banking.repository.CustomerRepository;

import java.util.List;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository, CustomerRepository customerRepository) {
        this.accountRepository = accountRepository;
        this.customerRepository = customerRepository;
    }

    public AccountResponse createAccount(CreateAccountRequest account) {

        Customer customer = customerRepository.findById(account.getCustomerId())
                .orElseThrow(()-> new NotFoundException("Customer not found"));

        Account newAccount = new Account();
        newAccount.setIban(account.getIban());
        newAccount.setBalance(account.getBalance());
        newAccount.setCurrency(account.getCurrency());
        newAccount.setType(account.getType());
        newAccount.setCustomer(customer);
        accountRepository.save(newAccount);

        return toResponse(newAccount);
    }

    public AccountResponse findById(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(()-> new NotFoundException("Account not found"));
        return toResponse(account);
    }

    public AccountResponse findByIban(String iban) {
        Account account = accountRepository.findByIban(iban)
                .orElseThrow(()-> new NotFoundException("Account not found"));
        return toResponse(account);
    }

    public List<AccountResponse> findByCustomerId(Long customerId) {

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(()-> new NotFoundException("Customer not found"));

        return accountRepository.findByCustomerId(customerId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<AccountResponse> findAll() {
        return accountRepository.findAll().stream()
                .map(this::toResponse).toList();
    }

    public void deleteAccount(Long id) {
        Account existingAccount = accountRepository.findById(id)
                .orElseThrow(()-> new NotFoundException("Account with id " + id + " not found"));
        accountRepository.delete(existingAccount);

    }

    private AccountResponse toResponse(Account account) {
        return new AccountResponse(
                account.getId(),
                account.getIban(),
                account.getBalance(),
                account.getCurrency(),
                account.getType(),
                account.getCustomer().getId()
        );
    }
}
