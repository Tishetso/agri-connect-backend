package com.agriconnect.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

   /* @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // ✅ Disable CSRF for API requests
                .cors(cors -> cors.disable()) // (optional) disable if you handle CORS elsewhere
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/login", "/api/register", "/api/test").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(login -> login.disable()) // disable form login
                .httpBasic(basic -> basic.disable()); // disable basic auth

        return http.build();
    }*/
   @Bean
   public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
       http
               .csrf(csrf -> csrf.disable())
               .authorizeHttpRequests(auth -> auth
                       .anyRequest().permitAll()
               )
               .formLogin(form -> form.disable())
               .httpBasic(basic -> basic.disable());
       return http.build();
   }
}
