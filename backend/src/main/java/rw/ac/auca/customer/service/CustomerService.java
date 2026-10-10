package rw.ac.auca.customer.service;

import rw.ac.auca.customer.domain.Customer;

import java.util.List;

public interface CustomerService {

    Customer registerCustomer(Customer theCustomer);
    Customer updateCustomer(Customer theCustomer);
    void deleteCustomer(Customer theCustomer);
    Customer findCustomerById(Customer theCustomer);
    List<Customer> findAllCustomers();
}
