package com.agriconnect.backend.filter;

import com.agriconnect.backend.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            try {
                Claims claims = jwtUtil.extractClaims(token);
                String email = claims.getSubject();

                // Extract roles properly
                Object authoritiesClaim = claims.get("authorities");
                List<String> roles = null;

                if (authoritiesClaim instanceof List<?>) {
                    roles = ((List<?>) authoritiesClaim).stream()
                            .filter(obj -> obj instanceof String)
                            .map(obj -> (String) obj)
                            .collect(Collectors.toList());
                }

                // Fallback if using old "role" claim
                if (roles == null || roles.isEmpty()) {
                    String role = claims.get("role", String.class);
                    if (role != null) {
                        roles = List.of(role.startsWith("ROLE_") ? role : "ROLE_" + role.toUpperCase());
                    }
                }

                if (email != null && SecurityContextHolder.getContext().getAuthentication() == null && roles != null) {
                    var authorities = roles.stream()
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());

                    var auth = new UsernamePasswordAuthenticationToken(
                            email, null, authorities
                    );
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }

            } catch (Exception e) {
                // Invalid/expired token → just continue unauthenticated
                // Don't send error — let Spring Security handle it
            }
        }

        filterChain.doFilter(request, response);
    }
}