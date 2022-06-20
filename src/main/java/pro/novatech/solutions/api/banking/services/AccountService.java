package pro.novatech.solutions.api.banking.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.novatech.solutions.api.banking.entities.Account;
import pro.novatech.solutions.api.banking.entities.Customer;
import pro.novatech.solutions.api.banking.entities.Transaction;
import pro.novatech.solutions.api.banking.enums.AccountType;
import pro.novatech.solutions.api.banking.repositories.AccountRepository;
import pro.novatech.solutions.api.banking.repositories.TransactionRepository;
import pro.novatech.solutions.api.banking.services.exceptions.InsufficientBalanceException;

import java.time.Instant;
import java.util.Optional;

@Service
public class AccountService implements IAccountService{

    private AccountRepository accountRepository;
    private TransactionRepository transactionRepository;
    private final static String INSUFFICIENT_BALANCE_ERROR = "Your balance is insufficient for this operation";
    @Autowired
    public void setAccountRepository(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Autowired
    public void setCustomerRepository(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    /**
     *
     * @param customer
     * @param initialAmount
     * @return account
     */
    @Override
    public Account createBankAccount(Customer customer, Double initialAmount) {
        Account account = new Account();
        account.setAccountNumber(generateBankAccountNumber());
        account.setBalance(initialAmount);
        account.setCustomerId(customer.getId());
        account.setUpdatedAt(Instant.now());
        account.setCreatedAt(Instant.now());
        return accountRepository.save(account);
    }

    /**
     *
     * @param from
     * @param to
     * @param amount
     * @throws InsufficientBalanceException
     */
    @Override
    public void transfer(Account from, Account to, Double amount) throws InsufficientBalanceException {
        if(from.getBalance() < amount){
            throw new InsufficientBalanceException(INSUFFICIENT_BALANCE_ERROR);
        }

        from.setBalance(from.getBalance() - amount);
        accountRepository.save(from);

        Transaction transaction = new Transaction();
        transaction.setAccountId(from.getId());
        transaction.setAmount(amount);
        transaction.setType(AccountType.CREDIT.label);
        transaction.setCreatedAt(Instant.now());
        transaction.setUpdatedAt(Instant.now());

        transactionRepository.save(transaction);

        to.setBalance(to.getBalance() + amount);
        accountRepository.save(to);

        transaction = new Transaction();
        transaction.setAccountId(to.getId());
        transaction.setAmount(amount);
        transaction.setType(AccountType.DEBIT.label);
        transaction.setCreatedAt(Instant.now());
        transaction.setUpdatedAt(Instant.now());
        transactionRepository.save(transaction);



    }

    private long generateBankAccountNumber()
    {
        long accountNumber = (long) Math.floor(Math.random() * 90000000000L) + 10000000000L;
        Optional<Account> account = accountRepository.getByAccountNumber(accountNumber);
        if(!account.isPresent()) {
            return accountNumber;
        }
        return generateBankAccountNumber();
    }
}
