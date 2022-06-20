package pro.novatech.solutions.api.banking.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pro.novatech.solutions.api.banking.controllers.response.BaseResponse;
import pro.novatech.solutions.api.banking.entities.Customer;
import pro.novatech.solutions.api.banking.repositories.CustomerRepository;

import javax.validation.Valid;
import java.time.Instant;
import java.util.HashMap;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/customer")
public class CustomerController {

    @Autowired
    private CustomerRepository customerRepository;
    public final static String CUSTOMER_NOT_FOUND_ERROR = "Customer not found";
    private final static String DELETION_COMPLETED= "Successfully deleted";

    @GetMapping("")
    public ResponseEntity<BaseResponse> getAll() {
        return ResponseEntity.ok(new BaseResponse(true, "", customerRepository.findAll()));

    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse> get(@PathVariable Integer id) {
        Optional<Customer> customer = customerRepository.findById(id);
        return customer.map(value -> new ResponseEntity<>(new BaseResponse(true, "", value), HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(new BaseResponse(false, CUSTOMER_NOT_FOUND_ERROR, new HashMap<String, String>()), HttpStatus.NOT_FOUND));
    }

    @PostMapping("")
    public ResponseEntity<BaseResponse> create(@Valid @RequestBody Customer customer){
          customer.setCreatedAt(Instant.now());
          customer.setUpdatedAt(Instant.now());
          Optional<Customer> optCustomer = customerRepository.getByName(customer.getName());
          return optCustomer.map(value -> new ResponseEntity<>(new BaseResponse(true, "", value), HttpStatus.OK)).orElseGet(() ->  new ResponseEntity<>(new BaseResponse(true, "",customerRepository.save(customer)), HttpStatus.CREATED));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse> update(@Valid @RequestBody Customer customer, @PathVariable Integer id){
        Optional<Customer> optCustomer = customerRepository.findById(id);
        if(optCustomer.isPresent()){
            optCustomer.get().setName(customer.getName());
            optCustomer.get().setUpdatedAt(Instant.now());
            return ResponseEntity.ok(new BaseResponse(true, "", customerRepository.save(optCustomer.get())));
        }
        return new ResponseEntity<>(new BaseResponse(false, CUSTOMER_NOT_FOUND_ERROR, new HashMap<String, String>()), HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse> destroy(@PathVariable Integer id) {
        Optional<Customer> customer = customerRepository.findById(id);
        if(customer.isPresent()){
            customerRepository.delete(customer.get());
            return ResponseEntity.ok(new BaseResponse(true, DELETION_COMPLETED, new HashMap<String,String>()));
        }

        return new ResponseEntity<>(new BaseResponse(false, CUSTOMER_NOT_FOUND_ERROR, new HashMap<String, String>()), HttpStatus.NOT_FOUND);

    }

}
