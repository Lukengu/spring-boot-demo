package pro.novatech.solutions.api.banking.services;

import pro.novatech.solutions.api.banking.entities.Account;
import pro.novatech.solutions.api.banking.entities.Customer;
import pro.novatech.solutions.api.banking.repositories.AccountRepository;
import pro.novatech.solutions.api.banking.repositories.CustomerRepository;
import pro.novatech.solutions.api.banking.services.exceptions.InsufficientBalanceException;

public interface IAccountService {

    /**
     *
     * @param customer
     * @param initialAmount
     * @return account
     */
    Account createBankAccount(Customer customer, Double initialAmount);

    /**
     *
     * @param from
     * @param to
     * @param amount
     * @throws InsufficientBalanceException
     */
    void transfer(Account from, Account to, Double amount) throws InsufficientBalanceException;


}
