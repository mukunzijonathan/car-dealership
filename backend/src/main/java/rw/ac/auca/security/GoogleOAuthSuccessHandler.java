package rw.ac.auca.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import rw.ac.auca.customer.domain.Customer;
import rw.ac.auca.customer.repository.CustomerRepository;
import rw.ac.auca.messaging.AccountRegisteredEvent;
import rw.ac.auca.messaging.BrokerConfig;
import rw.ac.auca.user.domain.AppUser;
import rw.ac.auca.user.domain.Role;
import rw.ac.auca.user.repository.AppUserRepository;

import java.time.LocalDate;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class GoogleOAuthSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${cdms.frontend-url}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException {
        OAuth2User principal = (OAuth2User) authentication.getPrincipal();
        String email = String.valueOf(principal.getAttribute("email")).trim().toLowerCase();
        String name = principal.getAttribute("name") != null
                ? String.valueOf(principal.getAttribute("name"))
                : email;
        String googleId = String.valueOf(principal.getAttribute("sub"));

        AppUser user = appUserRepository.findByEmailIgnoreCase(email).orElseGet(AppUser::new);
        boolean created = user.getId() == null;
        if (created) {
            user.setEmail(email);
            user.setFullName(name);
            user.setRole(Role.CUSTOMER);
            user.setProvider("GOOGLE");
            user.setProviderId(googleId);
            appUserRepository.save(user);
        }

        Customer customer = customerRepository.findByEmailIgnoreCase(email).orElseGet(Customer::new);
        if (customer.getId() == null) {
            String[] parts = name.trim().split("\\s+", 2);
            customer.setFirstName(parts[0]);
            customer.setLastName(parts.length > 1 ? parts[1] : "Google");
            customer.setEmail(email);
            customer.setPhoneNumber("google-oauth");
            customer.setCreditLimit(80000000.0);
            customer.setRegisteredOn(LocalDate.now());
            customerRepository.save(customer);
            rabbitTemplate.convertAndSend(
                    BrokerConfig.EXCHANGE,
                    BrokerConfig.ACCOUNT_KEY,
                    new AccountRegisteredEvent(email, customer.getPhoneNumber(), name));
        }

        String token = jwtService.createToken(user);
        String redirect = frontendUrl + "/oauth/callback?token=" + URLEncoder.encode(token, StandardCharsets.UTF_8);
        response.sendRedirect(redirect);
    }
}
