package ua.kpi.banking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.kpi.banking.model.Customer;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByEmail(String email);
}
