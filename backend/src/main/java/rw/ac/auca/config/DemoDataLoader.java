package rw.ac.auca.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import rw.ac.auca.car.domain.Car;
import rw.ac.auca.car.domain.CarStatus;
import rw.ac.auca.car.repository.CarRepository;
import rw.ac.auca.customer.domain.Customer;
import rw.ac.auca.customer.repository.CustomerRepository;
import rw.ac.auca.employee.domain.Employee;
import rw.ac.auca.employee.domain.EmployeeRole;
import rw.ac.auca.employee.repository.EmployeeRepository;
import rw.ac.auca.user.domain.AppUser;
import rw.ac.auca.user.domain.Role;
import rw.ac.auca.user.repository.AppUserRepository;

import java.time.LocalDate;

@Component
@Order(2)
public class DemoDataLoader implements CommandLineRunner {

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CarRepository carRepository;

    @Override
    public void run(String... args) {
        seedUser("admin@cdms.rw", "Admin#123", "Jean Mugabo", Role.ADMIN);
        seedUser("manager@cdms.rw", "Manager#123", "Claire Iradukunda", Role.MANAGER);
        seedUser("agent@cdms.rw", "Agent#123", "Eric Nkusi", Role.SALES_AGENT);
        seedUser("customer@cdms.rw", "Customer#123", "Aline Uwase", Role.CUSTOMER);

        seedEmployee("Jean", "Mugabo", "admin@cdms.rw", "EMP-0001", EmployeeRole.ADMIN, 1200000);
        seedEmployee("Claire", "Iradukunda", "manager@cdms.rw", "EMP-0002", EmployeeRole.MANAGER, 800000);
        seedEmployee("Eric", "Nkusi", "agent@cdms.rw", "EMP-0003", EmployeeRole.SALES_AGENT, 450000);

        seedCustomer("Aline", "Uwase", "customer@cdms.rw", "+250788111222");
        seedUser("patrick.habimana@gmail.com", "Customer#123", "Patrick Habimana", Role.CUSTOMER);
        seedUser("diane.uwimana@gmail.com", "Customer#123", "Diane Uwimana", Role.CUSTOMER);
        seedCustomer("Patrick", "Habimana", "patrick.habimana@gmail.com", "+250788333444");
        seedCustomer("Diane", "Uwimana", "diane.uwimana@gmail.com", "+250788555666");

        seedCar("1HGCM82633A004352", "Toyota", "Land Cruiser", 2022, 98000000, "landcruiser.jpg");
        seedCar("WBA3A5C50CF255891", "BMW", "X5", 2023, 75000000, "bmwx5.jpg");
        seedCar("JN1TANMAN00000001", "Nissan", "Patrol", 2021, 62000000, "nissanpatrol.jpg");
        seedCar("MERCEDESGLE450001", "Mercedes", "GLE 450", 2024, 88000000, "gle.jpg");
        seedCar("TOYOTACOROLLA0001", "Toyota", "Corolla", 2023, 18500000, "corolla.jpg");
        seedCar("PORSCHE9110000001", "Porsche", "911", 2022, 145000000, "porsche911.jpg");
        seedCar("TOYOTAPRIUS000001", "Toyota", "Prius", 2024, 24000000, "prius.jpg");
        seedCar("HONDAACRV20230001", "Honda", "CR-V", 2023, 28500000, "crv.jpg");
        seedCar("AUDIQ7XXXX2024001", "Audi", "Q7", 2024, 92000000, "bmwx5.jpg");
        seedCar("VOLKSWAGENGOLF001", "Volkswagen", "Golf", 2022, 19500000, "golf.jpg");
        seedCar("HYUNDAITUCSON0001", "Hyundai", "Tucson", 2023, 26800000, "tucson.jpg");
        seedCar("FORDRANGER2023001", "Ford", "Ranger", 2023, 41000000, "bmw330.jpg");
        seedCar("SUBARUFORESTER001", "Subaru", "Forester", 2022, 30500000, "forester.jpg");
        seedCar("KIASPORTAGE202301", "Kia", "Sportage", 2024, 24900000, "sportage.jpg");
        seedCar("TOYOTARAV42024001", "Toyota", "RAV4", 2024, 33500000, "rav4.jpg");
        seedCar("BMW3SERIES2022001", "BMW", "330i", 2022, 54000000, "rangerover.jpg");
        seedCar("MAZDACX5202300001", "Mazda", "CX-5", 2023, 32000000, "cx5.jpg");
        seedCar("MERCEDESC20000001", "Mercedes", "C 200", 2023, 72000000, "q7.jpg");
        seedCar("TOYOTAHILUX202301", "Toyota", "Hilux", 2024, 48000000, "hilux.jpg");
        seedCar("HONDAACIVIC202301", "Honda", "Civic", 2022, 21500000, "civic.jpg");
        seedCar("RANGEROVER2023001", "Land Rover", "Range Rover Sport", 2023, 165000000, "landcruiser.jpg");
        seedCar("TOYOTACAMRY202301", "Toyota", "Camry", 2024, 36000000, "camry.jpg");
        seedCar("PEUGEOT3008202301", "Peugeot", "3008", 2022, 27500000, "peugeot.jpg");
        seedCar("SUZUKISWIFT202301", "Suzuki", "Swift", 2024, 16800000, "swift.jpg", CarStatus.SOLD);
        seedCar("ISUZUDMAX20230001", "Isuzu", "D-Max", 2023, 39500000, "dmax.jpg", CarStatus.RESERVED);
    }

    private void seedUser(String email, String password, String name, Role role) {
        if (appUserRepository.existsByEmailIgnoreCase(email)) {
            return;
        }
        AppUser user = new AppUser();
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setFullName(name);
        user.setRole(role);
        user.setProvider("LOCAL");
        appUserRepository.save(user);
    }

    private void seedEmployee(String first, String last, String email, String number, EmployeeRole role, double salary) {
        if (employeeRepository.existsByEmailIgnoreCase(email)
                || employeeRepository.existsByEmployeeNumberIgnoreCase(number)) {
            return;
        }
        Employee employee = new Employee();
        employee.setFirstName(first);
        employee.setLastName(last);
        employee.setEmail(email);
        employee.setPhoneNumber("+250788000000");
        employee.setEmployeeNumber(number);
        employee.setHireDate(LocalDate.of(2021, 3, 12));
        employee.setRole(role);
        employee.setSalary(salary);
        employeeRepository.save(employee);
    }

    private void seedCustomer(String first, String last, String email, String phone) {
        customerRepository.findByEmailIgnoreCase(email).ifPresentOrElse(existing -> {
            if (existing.getCreditLimit() == null || existing.getCreditLimit() < 200000000.0) {
                existing.setCreditLimit(200000000.0);
                customerRepository.save(existing);
            }
        }, () -> {
            Customer customer = new Customer();
            customer.setFirstName(first);
            customer.setLastName(last);
            customer.setEmail(email);
            customer.setPhoneNumber(phone);
            customer.setCreditLimit(200000000.0);
            customer.setRegisteredOn(LocalDate.now());
            customerRepository.save(customer);
        });
    }

    private void seedCar(String vin, String brand, String model, int year, double price, String imageFile) {
        seedCar(vin, brand, model, year, price, imageFile, CarStatus.AVAILABLE);
    }

    private void seedCar(String vin, String brand, String model, int year, double price, String imageFile, CarStatus status) {
        carRepository.findByVinIgnoreCase(vin).ifPresentOrElse(existing -> {
            existing.setBrand(brand);
            existing.setModel(model);
            existing.setManufactureYear(year);
            existing.setPrice(price);
            existing.setImageFile(imageFile);
            carRepository.save(existing);
        }, () -> {
            Car car = new Car();
            car.setVin(vin);
            car.setBrand(brand);
            car.setModel(model);
            car.setManufactureYear(year);
            car.setPrice(price);
            car.setStatus(status);
            car.setImageFile(imageFile);
            carRepository.save(car);
        });
    }
}
