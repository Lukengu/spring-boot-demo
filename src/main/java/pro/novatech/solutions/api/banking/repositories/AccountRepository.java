package pro.novatech.solutions.api.banking.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import pro.novatech.solutions.api.banking.entities.Account;

import java.util.Optional;

public interface AccountRepository extends CrudRepository<Account, Integer> {
    @Query("SELECT a FROM Account a WHERE a.accountNumber = :accountNumber")
    Optional<Account> getByAccountNumber(long accountNumber);
}
