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
                return ResponseEntity.ok(Map.of("token", token, "role", user.getRole(), "name", user.getName(),
                        "region", user.getRegion()));
            }
        }
        System.out.println("Login attempt for: " + loginDto.getEmail());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Invalid credentials")); // ✅ JSON error
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserDto userDto) {

        // Check if email already exists
        Optional<User> existingUser = userRepository.findByEmail(userDto.getEmail());
        if (existingUser.isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT) // 409 Conflict
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

        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully");
    }

}
