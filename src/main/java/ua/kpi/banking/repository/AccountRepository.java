package ua.kpi.banking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.kpi.banking.model.Account;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByIdAndIsDeletedFalse(Long id);

    Optional<Account> findByIbanAndIsDeletedFalse(String iban);

    List<Account> findByCustomerIdAndIsDeletedFalse(Long customerId);

    List<Account> findAllByIsDeletedFalse();
}
