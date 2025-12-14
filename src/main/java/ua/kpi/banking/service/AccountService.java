package ua.kpi.banking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.kpi.banking.dto.account.AccountResponse;
import ua.kpi.banking.dto.account.CreateAccountRequest;
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
public class AccountService {

    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    private final CardRepository cardRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository, CustomerRepository customerRepository, CardRepository cardRepository) {
        this.accountRepository = accountRepository;
        this.customerRepository = customerRepository;
        this.cardRepository = cardRepository;
    }

    @Transactional
    public AccountResponse createAccount(CreateAccountRequest account) {

        Customer customer = customerRepository.findByIdAndIsDeletedFalse(account.getCustomerId())
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
        Account account = accountRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(()-> new NotFoundException("Account not found"));
        return toResponse(account);
    }

    public AccountResponse findByIban(String iban) {
        Account account = accountRepository.findByIbanAndIsDeletedFalse(iban)
                .orElseThrow(()-> new NotFoundException("Account not found"));
        return toResponse(account);
    }

    public List<AccountResponse> findByCustomerId(Long customerId) {

        Customer customer = customerRepository.findByIdAndIsDeletedFalse(customerId)
                .orElseThrow(()-> new NotFoundException("Customer not found"));

        return accountRepository.findByCustomerIdAndIsDeletedFalse(customerId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<AccountResponse> findAll() {
        return accountRepository.findAllByIsDeletedFalse().stream()
                .map(this::toResponse).toList();
    }

    @Transactional
    public void deleteAccount(Long id) {
        Account existingAccount = accountRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(()-> new NotFoundException("Account with id " + id + " not found"));

        List<Card> cards = cardRepository.findByAccountId(existingAccount.getId());

        for (Card card : cards) {
            card.setStatus(CardStatus.CLOSED);
        }
        cardRepository.saveAll(cards);

        existingAccount.setDeleted(true);
        accountRepository.save(existingAccount);
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
