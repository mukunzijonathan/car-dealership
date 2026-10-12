package rw.ac.auca.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import rw.ac.auca.user.domain.AppUser;
import rw.ac.auca.user.dto.AuthResponse;
import rw.ac.auca.user.dto.LoginRequest;
import rw.ac.auca.user.dto.RegisterRequest;
import rw.ac.auca.user.service.AuthService;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Value("${spring.security.oauth2.client.registration.google.client-id:not-configured}")
    private String googleClientId;

    @GetMapping("/status")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> status() {
        boolean google = googleClientId != null
                && !googleClientId.isBlank()
                && !"not-configured".equals(googleClientId);
        return Map.of("googleEnabled", google);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public AuthResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.OK)
    public AuthResponse register(@RequestBody RegisterRequest request) {
        return authService.registerCustomer(request);
    }

    @GetMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    public AuthResponse me() {
        AppUser user = authService.currentUser();
        return new AuthResponse(null, user.getEmail(), user.getFullName(), user.getRole());
    }
}
