package rw.ac.auca.sale.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rw.ac.auca.car.domain.Car;
import rw.ac.auca.car.domain.CarStatus;
import rw.ac.auca.car.repository.CarRepository;
import rw.ac.auca.customer.domain.Customer;
import rw.ac.auca.customer.repository.CustomerRepository;
import rw.ac.auca.employee.domain.Employee;
import rw.ac.auca.employee.repository.EmployeeRepository;
import rw.ac.auca.messaging.SaleCreatedEvent;
import rw.ac.auca.messaging.SaleEventPublisher;
import rw.ac.auca.sale.domain.Sale;
import rw.ac.auca.sale.domain.SaleStatus;
import rw.ac.auca.sale.repository.SaleRepository;
import rw.ac.auca.user.domain.AppUser;
import rw.ac.auca.user.service.AuthService;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class SaleServiceImpl implements SaleService {

    @Autowired
    private SaleRepository saleRepository;

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private SaleEventPublisher saleEventPublisher;

    @Autowired
    private AuthService authService;

    @Override
    @Transactional
    public Sale requestSale(Sale theSale) {
        if (theSale.getCar() == null || theSale.getCar().getId() == null) {
            throw new RuntimeException("A car is required");
        }
        Car car = carRepository.findById(theSale.getCar().getId())
                .orElseThrow(() -> new RuntimeException("Object not found!"));
        if (car.getStatus() != CarStatus.AVAILABLE) {
            throw new RuntimeException("This car is not available");
        }

        saleRepository.findByCarId(car.getId()).ifPresent(existing -> {
            if (existing.getStatus() == SaleStatus.REQUESTED) {
                throw new RuntimeException("This car already has a pending request");
            }
            if (existing.getStatus() == SaleStatus.APPROVED) {
                throw new RuntimeException("This car already has a sale record");
            }
        });

        Customer customer = resolveLoggedInCustomer();
        double amount = car.getPrice();
        if (customer.getCreditLimit() != null && amount > customer.getCreditLimit()) {
            throw new RuntimeException("This car exceeds your credit limit");
        }

        car.setStatus(CarStatus.RESERVED);
        carRepository.save(car);

        Sale sale = saleRepository.findByCarId(car.getId()).orElseGet(Sale::new);
        sale.setCar(car);
        sale.setCustomer(customer);
        sale.setEmployee(null);
        sale.setAmount(amount);
        sale.setSoldOn(null);
        sale.setStatus(SaleStatus.REQUESTED);
        sale.setNotes(theSale.getNotes());
        return saleRepository.save(sale);
    }

    @Override
    @Transactional
    public Sale approveSale(UUID saleId) {
        Sale sale = saleRepository.findById(saleId)
                .orElseThrow(() -> new RuntimeException("Object not found!"));
        if (sale.getStatus() != SaleStatus.REQUESTED) {
            throw new RuntimeException("Only a requested sale can be approved");
        }
        Employee employee = resolveEmployee(new Sale());
        Car car = sale.getCar();
        car.setStatus(CarStatus.SOLD);
        carRepository.save(car);

        sale.setEmployee(employee);
        sale.setStatus(SaleStatus.APPROVED);
        sale.setSoldOn(LocalDate.now());
        Sale saved = saleRepository.save(sale);
        publish(saved);
        return saved;
    }

    @Override
    @Transactional
    public Sale rejectSale(UUID saleId) {
        Sale sale = saleRepository.findById(saleId)
                .orElseThrow(() -> new RuntimeException("Object not found!"));
        if (sale.getStatus() != SaleStatus.REQUESTED) {
            throw new RuntimeException("Only a requested sale can be rejected");
        }
        Car car = sale.getCar();
        car.setStatus(CarStatus.AVAILABLE);
        carRepository.save(car);
        sale.setStatus(SaleStatus.REJECTED);
        sale.setEmployee(resolveEmployee(new Sale()));
        return saleRepository.save(sale);
    }

    @Override
    @Transactional
    public Sale registerSale(Sale theSale) {
        if (theSale.getCar() == null || theSale.getCar().getId() == null) {
            throw new RuntimeException("A car is required");
        }
        if (theSale.getCustomer() == null || theSale.getCustomer().getId() == null) {
            throw new RuntimeException("A customer is required");
        }

        Car car = carRepository.findById(theSale.getCar().getId())
                .orElseThrow(() -> new RuntimeException("Object not found!"));
        if (car.getStatus() != CarStatus.AVAILABLE && car.getStatus() != CarStatus.RESERVED) {
            throw new RuntimeException("Only AVAILABLE or RESERVED cars can be sold");
        }
        saleRepository.findByCarId(car.getId()).ifPresent(existing -> {
            if (existing.getStatus() == SaleStatus.APPROVED) {
                throw new RuntimeException("This car already has a sale record");
            }
        });

        Customer customer = customerRepository.findById(theSale.getCustomer().getId())
                .orElseThrow(() -> new RuntimeException("Object not found!"));
        Employee employee = resolveEmployee(theSale);

        double amount = theSale.getAmount() != null ? theSale.getAmount() : car.getPrice();
        if (amount <= 0) {
            throw new RuntimeException("Sale amount must be greater than zero");
        }
        if (customer.getCreditLimit() != null && amount > customer.getCreditLimit()) {
            throw new RuntimeException("Sale amount exceeds the customer credit limit");
        }

        car.setStatus(CarStatus.SOLD);
        carRepository.save(car);

        Sale sale = saleRepository.findByCarId(car.getId()).orElseGet(Sale::new);
        sale.setCar(car);
        sale.setCustomer(customer);
        sale.setEmployee(employee);
        sale.setAmount(amount);
        sale.setSoldOn(theSale.getSoldOn() != null ? theSale.getSoldOn() : LocalDate.now());
        sale.setStatus(SaleStatus.APPROVED);
        sale.setNotes(theSale.getNotes());
        Sale saved = saleRepository.save(sale);
        publish(saved);
        return saved;
    }

    private void publish(Sale saved) {
        saleEventPublisher.publishSaleCreated(new SaleCreatedEvent(
                saved.getId(),
                saved.getCar().getId(),
                saved.getCar().getVin(),
                saved.getCustomer().getEmail(),
                saved.getCustomer().getPhoneNumber(),
                saved.getEmployee().getEmail(),
                saved.getAmount()));
    }

    private Customer resolveLoggedInCustomer() {
        AppUser user = authService.currentUser();
        return customerRepository.findByEmailIgnoreCase(user.getEmail())
                .orElseGet(() -> {
                    String[] names = (user.getFullName() != null ? user.getFullName() : user.getEmail())
                            .trim().split("\\s+", 2);
                    Customer customer = new Customer();
                    customer.setFirstName(names[0]);
                    customer.setLastName(names.length > 1 ? names[1] : "Customer");
                    customer.setEmail(user.getEmail());
                    customer.setPhoneNumber("+250700000000");
                    customer.setCreditLimit(200000000.0);
                    customer.setRegisteredOn(LocalDate.now());
                    return customerRepository.save(customer);
                });
    }

    private Employee resolveEmployee(Sale theSale) {
        if (theSale.getEmployee() != null && theSale.getEmployee().getId() != null) {
            return employeeRepository.findById(theSale.getEmployee().getId())
                    .orElseThrow(() -> new RuntimeException("Object not found!"));
        }
        AppUser user = authService.currentUser();
        return employeeRepository.findByEmailIgnoreCase(user.getEmail())
                .orElseThrow(() -> new RuntimeException("Logged-in user is not linked to an employee record"));
    }

    @Override
    public Sale findSaleById(Sale theSale) {
        return saleRepository.findById(theSale.getId())
                .orElseThrow(() -> new RuntimeException("Object not found!"));
    }

    @Override
    public List<Sale> findAllSales() {
        return saleRepository.findAll();
    }

    @Override
    public List<Sale> findPendingSales() {
        return saleRepository.findByStatus(SaleStatus.REQUESTED);
    }

    @Override
    public List<Sale> findMySales() {
        Customer customer = resolveLoggedInCustomer();
        return saleRepository.findByCustomerId(customer.getId());
    }

    @Override
    public void deleteSale(Sale theSale) {
        throw new RuntimeException("Sales cannot be deleted; they are the dealership history");
    }
}
