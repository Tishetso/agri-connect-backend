package com.agriconnect.backend.util;

import com.agriconnect.backend.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtil {

    private static final long JWT_TOKEN_VALIDITY = 24 * 60 * 60 * 1000; // 24 hours
    private final SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                // Spring security expects authorities with "ROLE_" prefix
                .claim("authorities", List.of("ROLE_" + user.getRole().toUpperCase()))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY)) // 24hr
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // ✅ New overload — used for Drivers (no User object needed)
    public String generateToken(String email, String role) {
        return Jwts.builder()
                .setSubject(email)
                .claim("authorities", List.of("ROLE_" + role.toUpperCase()))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }


    public Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // ✅ ADD THIS METHOD - Extract email from token
    public String extractEmail(String token) {
        return extractClaims(token).getSubject();
    }

    // ✅ OPTIONAL: Add extractRole method for convenience
    public String extractRole(String token) {
        String role = extractClaims(token).get("role", String.class);
        if (role != null && role.startsWith("ROLE_")) {
            return role;
        }
        // Fallback
        return "ROLE_" + (role != null ? role.toUpperCase() : "USER");
    }

    public boolean isTokenValid(String token, User user) {
        final String email = extractClaims(token).getSubject();
        return (email.equals(user.getEmail()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    public String getRoleFromToken(String token) {
        String role = extractClaims(token).get("role", String.class);
        if (role != null && role.startsWith("ROLE_")) {
            return role;
        }
        // Fallback
        return "ROLE_" + role.toUpperCase();
    }

    // ✅ OPTIONAL: Validate token without user object
    public boolean isTokenValid(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }
}