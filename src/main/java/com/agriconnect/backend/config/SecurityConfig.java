package com.agriconnect.backend.config;

import com.agriconnect.backend.filter.JwtFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;


@Configuration
public class SecurityConfig {

    @Value("${app.cors.allowed-origins}")
    private List<String> allowedOrigins;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


   @Bean
   public SecurityFilterChain filterChain(HttpSecurity http, JwtFilter jwtFilter) throws Exception {
       http
               .csrf(csrf -> csrf.disable())
               .cors(cors -> {}) //enable cors (cross-origin resource sharing)
               .authorizeHttpRequests(auth -> auth
               .requestMatchers("/api/login", "/api/register").permitAll()
               .anyRequest().authenticated()
               )
               .formLogin(form -> form.disable())
               .httpBasic(basic -> basic.disable())
               .addFilterBefore(jwtFilter, org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);

       return http.build();
   }

   @Bean
    public org.springframework.web.cors.CorsConfigurationSource corsConfigurationSource(){
       org.springframework.web.cors.CorsConfiguration configuration = new org.springframework.web.cors.CorsConfiguration();
       configuration.setAllowedOrigins(allowedOrigins); // ✅ use injected origins
       configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
       configuration.setAllowedHeaders(List.of("*"));
       configuration.setAllowCredentials(true);

        org.springframework.web.cors.UrlBasedCorsConfigurationSource source = new org.springframework.web.cors.UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
   }
}
