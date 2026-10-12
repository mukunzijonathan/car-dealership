package rw.ac.auca.user.service;

import rw.ac.auca.user.domain.AppUser;
import rw.ac.auca.user.dto.AuthResponse;
import rw.ac.auca.user.dto.LoginRequest;
import rw.ac.auca.user.dto.RegisterRequest;

public interface AuthService {
    AuthResponse login(LoginRequest request);
    AuthResponse registerCustomer(RegisterRequest request);
    AppUser currentUser();
}
