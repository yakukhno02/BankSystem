package ua.kpi.banking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.kpi.banking.dto.transaction.CreateTransactionRequest;
import ua.kpi.banking.dto.transaction.TransactionResponse;
import ua.kpi.banking.model.Account;
import ua.kpi.banking.model.AccountType;
import ua.kpi.banking.model.Transaction;
import ua.kpi.banking.model.TransactionType;
import ua.kpi.banking.repository.AccountRepository;
import ua.kpi.banking.repository.TransactionRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository, AccountRepository accountRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

    @Transactional
    public TransactionResponse createTransaction(CreateTransactionRequest transaction) {

        if (transaction.getAmount() == null || transaction.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Amount must be greater than 0");
        }

        Transaction newTransaction = new Transaction();

        newTransaction.setAmount(transaction.getAmount());
        newTransaction.setType(transaction.getType());
        newTransaction.setDescription(transaction.getDescription());
        newTransaction.setDate(LocalDateTime.now());

        switch (transaction.getType()) {
            case transfer -> handleTransfer(transaction, newTransaction);
            case withdraw -> handleWithdraw(transaction, newTransaction);
            case deposit  -> handleDeposit(transaction, newTransaction);
        }

        transactionRepository.save(newTransaction);
        return toResponse(newTransaction);
    }

    public TransactionResponse getTransactionById(Long id) {
        return toResponse(transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found")));
    }

    public List<TransactionResponse> getAllByFromAccountId(Long accountId) {
        return transactionRepository.findByFromAccountId(accountId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<TransactionResponse> getAllByToAccountId(Long accountId) {
        return transactionRepository.findByToAccountId(accountId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<TransactionResponse> getByType(TransactionType type) {
        return transactionRepository.findByType(type)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private TransactionResponse toResponse(Transaction transaction) {
        return new TransactionResponse(
                transaction.getId(),
                transaction.getType(),
                transaction.getAmount(),
                transaction.getCurrency(),
                transaction.getDate(),
                transaction.getFromAccount() != null
                        ? transaction.getFromAccount().getId()
                        : null,
                transaction.getToAccount() != null
                        ? transaction.getToAccount().getId()
                        : null,
                transaction.getDescription());
    }


    private void handleTransfer(CreateTransactionRequest transaction, Transaction newTransaction) {

        Account from = accountRepository.findById(transaction.getFromAccountId())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        Account to = accountRepository.findById(transaction.getToAccountId())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (from.getType() == AccountType.deposit) {
            throw new RuntimeException("Transfers from deposit account are not allowed");
        }

        if (!from.getCurrency().equals(to.getCurrency())) {
            throw new RuntimeException("Currencies don't match");
        }

        if (from.getBalance().compareTo(transaction.getAmount()) < 0) {
            throw new RuntimeException("Insufficient funds");
        }

        from.setBalance(from.getBalance().subtract(transaction.getAmount()));
        to.setBalance(to.getBalance().add(transaction.getAmount()));

        newTransaction.setFromAccount(from);
        newTransaction.setToAccount(to);
        newTransaction.setCurrency(from.getCurrency());

        accountRepository.save(from);
        accountRepository.save(to);
    }

    private void handleWithdraw(CreateTransactionRequest transaction, Transaction newTransaction) {
        Account account = accountRepository.findById(transaction.getFromAccountId())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (account.getBalance().compareTo(transaction.getAmount()) < 0) {
            throw new RuntimeException("Insufficient funds");
        }

        if (account.getType() == AccountType.deposit) {
            throw new RuntimeException("Withdraw from deposit account are not allowed");
        }

        account.setBalance(account.getBalance().subtract(transaction.getAmount()));

        newTransaction.setFromAccount(account);
        newTransaction.setCurrency(account.getCurrency());

        accountRepository.save(account);
    }

    private void handleDeposit(CreateTransactionRequest transaction, Transaction newTransaction) {
        Account account = accountRepository.findById(transaction.getToAccountId())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        account.setBalance(account.getBalance().add(transaction.getAmount()));

        newTransaction.setToAccount(account);
        newTransaction.setCurrency(account.getCurrency());

        accountRepository.save(account);
    }

    public void deleteTransaction(Long id) {
        Transaction existingTransaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction with id" + id + "not found"));
        transactionRepository.deleteById(id);
    }

}
