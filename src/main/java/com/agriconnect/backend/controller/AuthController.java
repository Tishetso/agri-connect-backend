package com.agriconnect.backend.controller;

import com.agriconnect.backend.dto.LoginDto;
import com.agriconnect.backend.dto.UserDto;
import com.agriconnect.backend.model.User;
import com.agriconnect.backend.repository.UserRepository;
import com.agriconnect.backend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginDto loginDto) {
        Optional<User> userOpt = userRepository.findByEmail(loginDto.getEmail());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
                String token = jwtUtil.generateToken(user);
                return ResponseEntity.ok(Map.ofEntries(
                        Map.entry("token",     token),
                        Map.entry("id",        user.getId()),
                        Map.entry("name",      user.getName()),
                        Map.entry("surname",   user.getSurname()),
                        Map.entry("email",     user.getEmail()),
                        Map.entry("role",      user.getRole()),
                        Map.entry("region",    user.getRegion()    != null ? user.getRegion()    : ""),
                        Map.entry("phone",     user.getPhone()     != null ? user.getPhone()     : ""),
                        Map.entry("avatarUrl", user.getAvatarUrl() != null ? user.getAvatarUrl() : ""),
                        Map.entry("status",    user.getStatus()),
                        Map.entry("createdAt", user.getCreatedAt().toString())
                ));
            }
        }
        System.out.println("Login attempt for: " + loginDto.getEmail());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Invalid credentials"));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserDto userDto) {

        if ("ADMIN".equalsIgnoreCase(userDto.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Admin accounts cannot be self-registered"));
        }

        Optional<User> existingUser = userRepository.findByEmail(userDto.getEmail());
        if (existingUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Email already exists");
        }

        User user = new User();
        user.setName(userDto.getName());
        user.setSurname(userDto.getSurname());
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setIdNumber(userDto.getIdNumber());
        user.setRole(userDto.getRole());
        user.setRegion(userDto.getRegion());
        user.setLatitude(userDto.getLatitude());
        user.setLongitude(userDto.getLongitude());
        if (userDto.getPhone() != null) user.setPhone(userDto.getPhone());
        user.setCreatedAt(LocalDateTime.now());

        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully");
    }
}