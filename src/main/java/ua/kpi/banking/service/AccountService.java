package ua.kpi.banking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.kpi.banking.model.Account;
import ua.kpi.banking.repository.AccountRepository;

import java.util.List;
import java.util.Optional;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Account createAccount(Account account) {
        return accountRepository.save(account);
    }

    public Optional<Account> findById(Long id) {
        return accountRepository.findById(id);
    }

    public Optional<Account> findByIban(String iban) {
        return accountRepository.findByIban(iban);
    }

    public List<Account> findByCustomerId(Long customerId) {
        return accountRepository.findByCustomerId(customerId);
    }

    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    public void deleteAccount(Long id) {
        Account existingAccount = accountRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Account with id " + id + " not found"));
        accountRepository.delete(existingAccount);

    }
}
