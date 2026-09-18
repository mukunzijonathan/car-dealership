package rw.ac.auca.customer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rw.ac.auca.customer.domain.Customer;
import rw.ac.auca.customer.repository.CustomerRepository;
import rw.ac.auca.employee.repository.EmployeeRepository;

import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public Customer registerCustomer(Customer theCustomer) {
        validateCustomer(theCustomer, true);
        theCustomer.setEmail(theCustomer.getEmail().trim().toLowerCase());
        return customerRepository.save(theCustomer);
    }

    @Override
    public Customer updateCustomer(Customer theCustomer) {
        Customer found = findCustomerById(theCustomer);
        validateCustomer(theCustomer, false);
        found.setFirstName(theCustomer.getFirstName().trim());
        found.setLastName(theCustomer.getLastName().trim());
        found.setEmail(theCustomer.getEmail().trim().toLowerCase());
        found.setPhoneNumber(theCustomer.getPhoneNumber().trim());
        found.setCreditLimit(theCustomer.getCreditLimit());
        return customerRepository.save(found);
    }

    @Override
    public void deleteCustomer(Customer theCustomer) {
        Customer found = findCustomerById(theCustomer);
        customerRepository.delete(found);
    }

    @Override
    public Customer findCustomerById(Customer theCustomer) {
        return customerRepository.findById(theCustomer.getId())
                .orElseThrow(() -> new RuntimeException("Object not found!"));
    }

    @Override
    public List<Customer> findAllCustomers() {
        return customerRepository.findAll();
    }

    private void validateCustomer(Customer theCustomer, boolean creating) {
        if (blank(theCustomer.getFirstName()) || blank(theCustomer.getLastName())) {
            throw new RuntimeException("First name and last name are required");
        }
        if (blank(theCustomer.getEmail()) || !theCustomer.getEmail().contains("@")) {
            throw new RuntimeException("A valid email is required");
        }
        if (blank(theCustomer.getPhoneNumber())) {
            throw new RuntimeException("Phone number is required");
        }
        if (theCustomer.getCreditLimit() == null || theCustomer.getCreditLimit() < 0) {
            throw new RuntimeException("Credit limit cannot be negative");
        }
        String email = theCustomer.getEmail().trim().toLowerCase();
        customerRepository.findByEmailIgnoreCase(email).ifPresent(existing -> {
            if (creating || !existing.getId().equals(theCustomer.getId())) {
                throw new RuntimeException("A customer with this email already exists");
            }
        });
        if (employeeRepository.existsByEmailIgnoreCase(email)) {
            throw new RuntimeException("This email already belongs to an employee");
        }
    }

    private boolean blank(String value) {
        return value == null || value.isBlank();
    }
}
