package rw.ac.auca.dealership.bean;

import rw.ac.auca.dealership.dao.CustomerDAO;
import rw.ac.auca.dealership.model.Customer;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

@ManagedBean(name = "customerBean")
@ViewScoped
public class CustomerBean implements Serializable {
    private CustomerDAO dao = new CustomerDAO();
    private Customer customer = new Customer();
    private List<Customer> customers;

    public String save() {
        dao.save(customer);
        customer = new Customer();
        customers = null;
        return null;
    }

    public String edit(Customer c) {
        this.customer = c;
        return null;
    }

    public String delete(Long id) {
        dao.delete(id);
        customers = null;
        return null;
    }

    public List<Customer> getCustomers() {
        if (customers == null) customers = dao.findAll();
        return customers;
    }
    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
}