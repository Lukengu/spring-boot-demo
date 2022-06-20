package pro.novatech.solutions.api.banking.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import pro.novatech.solutions.api.banking.entities.Customer;

import java.util.Optional;

public interface CustomerRepository extends CrudRepository<Customer, Integer> {
    @Query("SELECT c FROM Customer c WHERE c.name = :name")
    Optional<Customer> getByName(String name);
}
