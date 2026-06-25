package ua.kpi.banking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.kpi.banking.model.Account;
import ua.kpi.banking.model.AccountStatus;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByIdAndStatus(Long id, AccountStatus status);

    Optional<Account> findByIbanAndStatus(String iban, AccountStatus status);

    List<Account> findByCustomer_IdAndStatus(Long customerId, AccountStatus status);

    List<Account> findAllByStatus(AccountStatus status);
}
