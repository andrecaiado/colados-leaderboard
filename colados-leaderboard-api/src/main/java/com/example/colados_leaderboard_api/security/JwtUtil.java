package com.example.colados_leaderboard_api.security;

import com.example.colados_leaderboard_api.entity.AppUser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

@Slf4j
@Getter
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration-ms}")
    private int jwtExpirationInMs;

    private SecretKey key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(UserDetails userDetails) {
        long now = System.currentTimeMillis();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claim("roles", roles)
                .claim("aud", "colados-leaderboard-client")
                .issuer("colados-leaderboard-api")
                .issuedAt(new Date(now))
                .expiration(new Date(now + jwtExpirationInMs))
                .signWith(key)
                .compact();
    }

    public String generateTokenForOAuth(AppUser appUser) {
        List<String> roles = appUser.getRoles().stream()
                .map(Enum::name)
                .toList();
        String email = appUser.getEmail();
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .subject(email)
                .claim("roles", roles)
                .claim("aud", "colados-leaderboard-client")
                .claim("oauth", true)
                .issuer("colados-leaderboard-api")
                .issuedAt(new Date(now))
                .expiration(new Date(now + jwtExpirationInMs))
                .signWith(key)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parser().verifyWith(key).build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }
}
