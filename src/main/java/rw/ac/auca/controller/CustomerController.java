package rw.ac.auca.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import rw.ac.auca.customer.domain.Customer;
import rw.ac.auca.customer.service.CustomerService;

import java.util.List;
import java.util.UUID;

@RestController
@Controller
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @GetMapping("/search-all")
    @ResponseStatus(HttpStatus.OK)
    public List<Customer> findAllRecords() {
        return customerService.findAllCustomers();
    }

    @GetMapping("/search-by-id/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Customer findById(@PathVariable UUID id) {
        Customer probe = new Customer();
        probe.setId(id);
        return customerService.findCustomerById(probe);
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.OK)
    public Customer createCustomer(@RequestBody Customer theCustomer) {
        return customerService.registerCustomer(theCustomer);
    }

    @PutMapping("/update")
    @ResponseStatus(HttpStatus.OK)
    public Customer updateCustomer(@RequestBody Customer theCustomer) {
        return customerService.updateCustomer(theCustomer);
    }

    @DeleteMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.OK)
    public String deleteCustomer(@PathVariable UUID id) {
        Customer probe = new Customer();
        probe.setId(id);
        customerService.deleteCustomer(probe);
        return "Deleted";
    }
}
