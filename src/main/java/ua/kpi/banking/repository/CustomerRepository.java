package ua.kpi.banking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.kpi.banking.model.Customer;
import ua.kpi.banking.model.CustomerStatus;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByIdAndStatus(Long id, CustomerStatus status);

    Optional<Customer> findByEmailAndStatus(String email, CustomerStatus status);

    List<Customer> findAllByStatus(CustomerStatus status);
}
