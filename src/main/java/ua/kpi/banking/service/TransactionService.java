package ua.kpi.banking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.kpi.banking.dto.transaction.CreateTransactionRequest;
import ua.kpi.banking.dto.transaction.TransactionResponse;
import ua.kpi.banking.model.Account;
import ua.kpi.banking.model.Transaction;
import ua.kpi.banking.model.TransactionType;
import ua.kpi.banking.repository.AccountRepository;
import ua.kpi.banking.repository.TransactionRepository;

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

    public TransactionResponse createTransaction(CreateTransactionRequest transaction) {

        Transaction newTransaction = new Transaction();

        newTransaction.setAmount(newTransaction.getAmount());
        newTransaction.setType(newTransaction.getType());
        newTransaction.setDescription(newTransaction.getDescription());
        newTransaction.setCurrency(newTransaction.getCurrency());
        newTransaction.setDate(newTransaction.getDate());

        if (transaction.getFromAccountId() != null) {
            Account fromAccount = accountRepository.findById(transaction.getFromAccountId())
                    .orElseThrow(() -> new RuntimeException("From Account not found"));
            newTransaction.setFromAccount(fromAccount);
        }

        if (transaction.getToAccountId() != null) {
            Account toAccount = accountRepository.findById(transaction.getToAccountId())
                    .orElseThrow(() -> new RuntimeException("To Account not found"));
            newTransaction.setToAccount(toAccount);
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

    public void deleteTransaction(Long id) {
        Transaction existingTransaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction with id" + id + "not found"));
        transactionRepository.deleteById(id);
    }

    private TransactionResponse toResponse(Transaction transaction) {
        return new TransactionResponse(
                transaction.getId(),
                transaction.getType().name(),
                transaction.getAmount(),
                transaction.getCurrency(),
                transaction.getDate(),
                transaction.getFromAccount().getId(),
                transaction.getToAccount().getId(),
                transaction.getDescription());
    }
}
