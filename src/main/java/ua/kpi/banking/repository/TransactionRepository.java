package ua.kpi.banking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.kpi.banking.model.Transaction;
import ua.kpi.banking.model.TransactionType;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByFromAccountId(Long accountId);

    List<Transaction> findByToAccountId(Long accountId);

    List<Transaction> findByType(TransactionType type);
}
