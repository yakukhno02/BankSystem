package ua.kpi.banking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ua.kpi.banking.dto.analytics.CustomerTransactionStats;
import ua.kpi.banking.model.Customer;

import java.util.List;

@Repository
public interface AnalyticsRepository extends JpaRepository<Customer, Long> {

    @Query(value = """
        SELECT
            c.customer_id AS customerId,
            c.name AS name,
            c.surname AS surname,
            COUNT(t.transaction_id) AS transactionCount,
            SUM(t.amount) AS totalAmount,
            ROUND(AVG(t.amount), 2) AS avgTransactionAmount,
            MAX(t.amount) AS maxTransactionAmount
        FROM customer c
        JOIN account a ON a.customer_id = c.customer_id
        JOIN transactions t
            ON t.from_account_id = a.account_id
            OR t.to_account_id = a.account_id
        GROUP BY c.customer_id, c.name, c.surname
        ORDER BY totalAmount DESC
        """, nativeQuery = true)
    List<CustomerTransactionStats> getCustomerTransactionStats();
}
