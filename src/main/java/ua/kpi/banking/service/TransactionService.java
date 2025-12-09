package ua.kpi.banking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.kpi.banking.model.Transaction;
import ua.kpi.banking.model.TransactionType;
import ua.kpi.banking.repository.TransactionRepository;

import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public Transaction makeTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    public Optional <Transaction> getTransactionById(Long id) {
        return transactionRepository.findById(id);
    }

    public List<Transaction> getAllByFromAccountId(Long accountId) {
        return transactionRepository.findByFromAccountId(accountId);
    }

    public List<Transaction> getAllByToAccountId(Long accountId) {
        return transactionRepository.findByToAccountId(accountId);
    }

    public List<Transaction> getByType(TransactionType type) {
        return transactionRepository.findByType(type);
    }

    public void deleteTransaction(Long id) {
        Transaction existingTransaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction with id" + id + "not found"));
        transactionRepository.deleteById(id);
    }
}
