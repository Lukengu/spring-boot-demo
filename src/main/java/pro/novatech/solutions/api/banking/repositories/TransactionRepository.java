package pro.novatech.solutions.api.banking.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import pro.novatech.solutions.api.banking.entities.Transaction;

public interface TransactionRepository extends CrudRepository<Transaction, Integer> {
    @Query("SELECT t FROM  Transaction t  WHERE t.accountId = :accountId")
    Iterable<Transaction> getByAccountId(Integer accountId);

}
