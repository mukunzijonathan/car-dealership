package rw.ac.auca.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import rw.ac.auca.user.domain.Role;

@Getter
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String email;
    private String fullName;
    private Role role;
}
