package rw.ac.auca.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import rw.ac.auca.user.domain.AppUser;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    @Value("${cdms.jwt.secret}")
    private String secret;

    @Value("${cdms.jwt.expiration-ms}")
    private long expirationMs;

    public String createToken(AppUser user) {
        Date now = new Date();
        return Jwts.builder()
                .subject(user.getEmail())
                .claim("role", user.getRole().name())
                .claim("name", user.getFullName())
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expirationMs))
                .signWith(key())
                .compact();
    }

    public Claims parse(String token) {
        return Jwts.parser()
                .verifyWith(key())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey key() {
        byte[] bytes = secret.getBytes(StandardCharsets.UTF_8);
        if (bytes.length < 32) {
            bytes = (secret + "0123456789abcdef0123456789abcdef").getBytes(StandardCharsets.UTF_8);
        }
        return Keys.hmacShaKeyFor(bytes);
    }
}
