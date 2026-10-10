package rw.ac.auca.employee.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import rw.ac.auca.customer.repository.CustomerRepository;
import rw.ac.auca.employee.domain.Employee;
import rw.ac.auca.employee.domain.EmployeeRole;
import rw.ac.auca.employee.repository.EmployeeRepository;
import rw.ac.auca.user.domain.AppUser;
import rw.ac.auca.user.domain.Role;
import rw.ac.auca.user.repository.AppUserRepository;

import java.time.LocalDate;
import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private static final double MIN_MANAGER_SALARY = 500000;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Employee registerEmployee(Employee theEmployee) {
        normalize(theEmployee);
        validateEmployee(theEmployee, true);
        if (theEmployee.getPortalPassword() == null || theEmployee.getPortalPassword().length() < 8) {
            throw new RuntimeException("Set a portal password of at least 8 characters so this staff member can sign in");
        }
        Employee saved = employeeRepository.save(theEmployee);
        syncPortalAccount(saved, theEmployee.getPortalPassword(), true);
        return saved;
    }

    @Override
    public Employee updateEmployee(Employee theEmployee) {
        Employee found = findEmployeeById(theEmployee);
        normalize(theEmployee);
        if (found.getRole() == EmployeeRole.ADMIN
                && theEmployee.getRole() != EmployeeRole.ADMIN
                && employeeRepository.countByRole(EmployeeRole.ADMIN) <= 1) {
            throw new RuntimeException("The dealership must keep at least one ADMIN");
        }
        validateEmployee(theEmployee, false);
        found.setFirstName(theEmployee.getFirstName().trim());
        found.setLastName(theEmployee.getLastName().trim());
        found.setEmail(theEmployee.getEmail());
        found.setPhoneNumber(theEmployee.getPhoneNumber().trim());
        found.setEmployeeNumber(theEmployee.getEmployeeNumber());
        found.setHireDate(theEmployee.getHireDate());
        found.setRole(theEmployee.getRole());
        found.setSalary(theEmployee.getSalary());
        Employee saved = employeeRepository.save(found);
        syncPortalAccount(saved, theEmployee.getPortalPassword(), false);
        return saved;
    }

    @Override
    public void deleteEmployee(Employee theEmployee) {
        Employee found = findEmployeeById(theEmployee);
        if (found.getRole() == EmployeeRole.ADMIN
                && employeeRepository.countByRole(EmployeeRole.ADMIN) <= 1) {
            throw new RuntimeException("The last ADMIN cannot be deleted");
        }
        appUserRepository.findByEmailIgnoreCase(found.getEmail()).ifPresent(appUserRepository::delete);
        employeeRepository.delete(found);
    }

    private void syncPortalAccount(Employee employee, String rawPassword, boolean creating) {
        AppUser user = appUserRepository.findByEmailIgnoreCase(employee.getEmail()).orElseGet(AppUser::new);
        if (user.getId() != null && user.getRole() == Role.CUSTOMER) {
            throw new RuntimeException("This email already belongs to a customer account");
        }
        user.setEmail(employee.getEmail());
        user.setFullName(employee.getFirstName() + " " + employee.getLastName());
        user.setRole(Role.valueOf(employee.getRole().name()));
        user.setProvider("LOCAL");
        if (rawPassword != null && !rawPassword.isBlank()) {
            if (rawPassword.length() < 8) {
                throw new RuntimeException("Portal password must be at least 8 characters");
            }
            user.setPasswordHash(passwordEncoder.encode(rawPassword));
        } else if (creating) {
            throw new RuntimeException("Set a portal password of at least 8 characters so this staff member can sign in");
        }
        appUserRepository.save(user);
    }

    @Override
    public Employee findEmployeeById(Employee theEmployee) {
        return employeeRepository.findById(theEmployee.getId())
                .orElseThrow(() -> new RuntimeException("Object not found!"));
    }

    @Override
    public List<Employee> findAllEmployees() {
        return employeeRepository.findAll();
    }

    private void normalize(Employee theEmployee) {
        if (theEmployee.getEmail() != null) {
            theEmployee.setEmail(theEmployee.getEmail().trim().toLowerCase());
        }
        if (theEmployee.getEmployeeNumber() != null) {
            theEmployee.setEmployeeNumber(theEmployee.getEmployeeNumber().trim().toUpperCase());
        }
    }

    private void validateEmployee(Employee theEmployee, boolean creating) {
        if (blank(theEmployee.getFirstName()) || blank(theEmployee.getLastName())) {
            throw new RuntimeException("First name and last name are required");
        }
        if (blank(theEmployee.getEmail()) || !theEmployee.getEmail().contains("@")) {
            throw new RuntimeException("A valid email is required");
        }
        if (blank(theEmployee.getPhoneNumber())) {
            throw new RuntimeException("Phone number is required");
        }
        if (theEmployee.getEmployeeNumber() == null
                || !theEmployee.getEmployeeNumber().matches("^EMP-[0-9]{4}$")) {
            throw new RuntimeException("Employee number must match EMP-0001");
        }
        if (theEmployee.getHireDate() == null || theEmployee.getHireDate().isAfter(LocalDate.now())) {
            throw new RuntimeException("Hire date cannot be in the future");
        }
        if (theEmployee.getRole() == null) {
            throw new RuntimeException("Role is required");
        }
        if (theEmployee.getSalary() == null || theEmployee.getSalary() <= 0) {
            throw new RuntimeException("Salary must be greater than zero");
        }
        if ((theEmployee.getRole() == EmployeeRole.MANAGER || theEmployee.getRole() == EmployeeRole.ADMIN)
                && theEmployee.getSalary() < MIN_MANAGER_SALARY) {
            throw new RuntimeException("MANAGER and ADMIN salaries must be at least RWF 500,000");
        }
        employeeRepository.findByEmailIgnoreCase(theEmployee.getEmail()).ifPresent(existing -> {
            if (creating || !existing.getId().equals(theEmployee.getId())) {
                throw new RuntimeException("An employee with this email already exists");
            }
        });
        employeeRepository.findAll().forEach(existing -> {
            if (!creating && existing.getId().equals(theEmployee.getId())) {
                return;
            }
            if (existing.getEmployeeNumber().equalsIgnoreCase(theEmployee.getEmployeeNumber())) {
                throw new RuntimeException("Employee number is already assigned");
            }
        });
        if (customerRepository.existsByEmailIgnoreCase(theEmployee.getEmail())) {
            throw new RuntimeException("This email already belongs to a customer");
        }
        appUserRepository.findByEmailIgnoreCase(theEmployee.getEmail()).ifPresent(existing -> {
            if (existing.getRole() == Role.CUSTOMER) {
                throw new RuntimeException("This email already belongs to a customer account");
            }
        });
    }

    private boolean blank(String value) {
        return value == null || value.isBlank();
    }
}
