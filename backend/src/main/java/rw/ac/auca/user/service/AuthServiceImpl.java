package rw.ac.auca.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rw.ac.auca.customer.domain.Customer;
import rw.ac.auca.customer.repository.CustomerRepository;
import rw.ac.auca.employee.repository.EmployeeRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import rw.ac.auca.messaging.AccountRegisteredEvent;
import rw.ac.auca.messaging.BrokerConfig;
import rw.ac.auca.security.JwtService;
import rw.ac.auca.user.domain.AppUser;
import rw.ac.auca.user.domain.Role;
import rw.ac.auca.user.dto.AuthResponse;
import rw.ac.auca.user.dto.LoginRequest;
import rw.ac.auca.user.dto.RegisterRequest;
import rw.ac.auca.user.repository.AppUserRepository;

import java.time.LocalDate;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public AuthResponse login(LoginRequest request) {
        if (request.getEmail() == null || request.getPassword() == null) {
            throw new RuntimeException("Email and password are required");
        }
        AppUser user = appUserRepository.findByEmailIgnoreCase(request.getEmail().trim())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));
        if (user.getPasswordHash() == null || !passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid email or password");
        }
        return new AuthResponse(jwtService.createToken(user), user.getEmail(), user.getFullName(), user.getRole());
    }

    @Override
    @Transactional
    public AuthResponse registerCustomer(RegisterRequest request) {
        if (blank(request.getFirstName()) || blank(request.getLastName())) {
            throw new RuntimeException("First name and last name are required");
        }
        if (blank(request.getEmail()) || !request.getEmail().contains("@")) {
            throw new RuntimeException("A valid email is required");
        }
        if (blank(request.getPhoneNumber())) {
            throw new RuntimeException("Phone number is required");
        }
        if (blank(request.getPassword()) || request.getPassword().length() < 8) {
            throw new RuntimeException("Password must be at least 8 characters");
        }
        String email = request.getEmail().trim().toLowerCase();
        if (appUserRepository.existsByEmailIgnoreCase(email)
                || customerRepository.existsByEmailIgnoreCase(email)
                || employeeRepository.existsByEmailIgnoreCase(email)) {
            throw new RuntimeException("An account with this email already exists");
        }

        Customer customer = new Customer();
        customer.setFirstName(request.getFirstName().trim());
        customer.setLastName(request.getLastName().trim());
        customer.setEmail(email);
        customer.setPhoneNumber(request.getPhoneNumber().trim());
        customer.setCreditLimit(80000000.0);
        customer.setRegisteredOn(LocalDate.now());
        customerRepository.save(customer);

        AppUser user = new AppUser();
        user.setEmail(email);
        user.setFullName(customer.getFirstName() + " " + customer.getLastName());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.CUSTOMER);
        user.setProvider("LOCAL");
        appUserRepository.save(user);

        rabbitTemplate.convertAndSend(
                BrokerConfig.EXCHANGE,
                BrokerConfig.ACCOUNT_KEY,
                new AccountRegisteredEvent(email, customer.getPhoneNumber(), user.getFullName()));

        return new AuthResponse(jwtService.createToken(user), user.getEmail(), user.getFullName(), user.getRole());
    }

    private boolean blank(String value) {
        return value == null || value.isBlank();
    }

    @Override
    public AppUser currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new RuntimeException("You are not logged in");
        }
        return appUserRepository.findByEmailIgnoreCase(authentication.getName())
                .orElseThrow(() -> new RuntimeException("You are not logged in"));
    }
}
