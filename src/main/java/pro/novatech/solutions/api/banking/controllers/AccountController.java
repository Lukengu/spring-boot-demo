package pro.novatech.solutions.api.banking.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pro.novatech.solutions.api.banking.controllers.response.BaseResponse;
import pro.novatech.solutions.api.banking.entities.Account;
import pro.novatech.solutions.api.banking.entities.Customer;
import pro.novatech.solutions.api.banking.repositories.AccountRepository;
import pro.novatech.solutions.api.banking.repositories.CustomerRepository;
import pro.novatech.solutions.api.banking.repositories.TransactionRepository;
import pro.novatech.solutions.api.banking.services.IAccountService;
import pro.novatech.solutions.api.banking.services.exceptions.InsufficientBalanceException;

import java.util.HashMap;
import java.util.Optional;

import static pro.novatech.solutions.api.banking.controllers.CustomerController.CUSTOMER_NOT_FOUND_ERROR;

@RestController
@RequestMapping("/api/v1/account")
public class AccountController {

    @Autowired
    private IAccountService accountService;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private TransactionRepository transactionRepository;


    private final static String REQUIRED_FIELD_ERROR = "Required field missing (customer_id | initial_amount)";
    private final static String TRANSFER_FIELD_ERROR = "Required field missing (account_from | account_to | amount)";
    private final static String ACCOUNT_FROM_ERROR = "The account you are trying to debit from does not exists";
    private final static String ACCOUNT_ERROR = "The account does not exists";
    private final static String ACCOUNT_TO_ERROR = "The account you are trying to credit does not exists";
    private final static String OPERATION_COMPLETED = "Operation Completed";


    @PostMapping("/create")
    public ResponseEntity<BaseResponse> create(@RequestBody HashMap<String, Object> body){
        if(!body.containsKey("customer_id") && !body.containsKey("initial_amount")){
            return new ResponseEntity<>(new BaseResponse(false, REQUIRED_FIELD_ERROR,
                    new HashMap<String, String>()), HttpStatus.BAD_REQUEST);
        }

        Integer customerId = Integer.parseInt(body.get("customer_id").toString());
        Double initialAmount = Double.parseDouble(body.get("initial_amount").toString());

        Optional<Customer> customer = customerRepository.findById(customerId);
        if(customer.isPresent()) {
            return ResponseEntity.ok(new BaseResponse(true, "",
                    accountService.createBankAccount(customer.get(), initialAmount)));
        }

          return new ResponseEntity<>(new BaseResponse(false, CUSTOMER_NOT_FOUND_ERROR,
                                                       new HashMap<String, String>()), HttpStatus.NOT_FOUND);
        

    }
    @PostMapping("/transfer")
    public ResponseEntity<BaseResponse> transfer(@RequestBody HashMap<String, Object> body){
        if(!body.containsKey("account_from") && !body.containsKey("account_to") && !body.containsKey("amount") ){
            return new ResponseEntity<>(new BaseResponse(false, TRANSFER_FIELD_ERROR,
                    new HashMap<String, String>()), HttpStatus.BAD_REQUEST);
        }

        long accountFrom = Long.parseLong(body.get("account_from").toString());
        long accountTo = Long.parseLong(body.get("account_to").toString());
        double amount = Double.parseDouble(body.get("amount").toString());

        Optional<Account> from = accountRepository.getByAccountNumber(accountFrom);
        Optional<Account>  to = accountRepository.getByAccountNumber(accountTo);

        if(from.isEmpty()) {
            return new ResponseEntity<>(new BaseResponse(false, ACCOUNT_FROM_ERROR,
                    new HashMap<String, String>()), HttpStatus.NOT_FOUND);
        }

        if(to.isEmpty()) {
            return new ResponseEntity<>(new BaseResponse(false, ACCOUNT_TO_ERROR,
                    new HashMap<String, String>()), HttpStatus.NOT_FOUND);
        }

        try {
            accountService.transfer(from.get(), to.get(), amount);
            return ResponseEntity.ok(new BaseResponse(true, OPERATION_COMPLETED, new HashMap<String, String>()));
        } catch (InsufficientBalanceException e) {
            return new ResponseEntity(new BaseResponse(false, e.getMessage(), new HashMap<String,String>()),
                    HttpStatus.UNPROCESSABLE_ENTITY);

        }


    }

    @GetMapping("/balance/{accountNumber}")
    public ResponseEntity<BaseResponse>  balance(@PathVariable long accountNumber){
        Optional<Account> account = accountRepository.getByAccountNumber(accountNumber);

        if(account.isEmpty()) {
            return new ResponseEntity<>(new BaseResponse(false, ACCOUNT_ERROR,
                    new HashMap<String, String>()), HttpStatus.NOT_FOUND);
        }
        HashMap<String,Object> response = new HashMap<>();
        response.put("balance", account.get().getBalance());
        return ResponseEntity.ok(new BaseResponse(true, "", response));

    }
    @GetMapping("/transactions/{accountNumber}")
    public ResponseEntity<BaseResponse>  transaction(@PathVariable long accountNumber){
        Optional<Account> account = accountRepository.getByAccountNumber(accountNumber);

        if(account.isEmpty()) {
            return new ResponseEntity<>(new BaseResponse(false, ACCOUNT_ERROR,
                    new HashMap<String, String>()), HttpStatus.NOT_FOUND);
        }
        HashMap<String,Object> response = new HashMap<>();
        response.put("transactions", transactionRepository.getByAccountId(account.get().getId()));
        return ResponseEntity.ok(new BaseResponse(true, "", response));

    }



}
