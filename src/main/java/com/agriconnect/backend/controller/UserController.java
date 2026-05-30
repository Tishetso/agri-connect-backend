package com.agriconnect.backend.controller;

import com.agriconnect.backend.model.User;
import com.agriconnect.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Authentication authentication) {
        String email = authentication.getName();
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "User not found"));
        }

        User user = userOpt.get();
        return ResponseEntity.ok(Map.of(
                "id",        user.getId(),
                "name",      user.getName(),
                "surname",   user.getSurname(),
                "email",     user.getEmail(),
                "role",      user.getRole(),
                "status",    user.getStatus(),
                "region",    user.getRegion() != null ? user.getRegion() : "",
                "phone",     user.getPhone()  != null ? user.getPhone()  : "",
                "createdAt", user.getCreatedAt().toString()
        ));
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(Authentication authentication,
                                           @RequestBody Map<String, String> updates) {
        String email = authentication.getName();
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "User not found"));
        }

        User user = userOpt.get();

        if (updates.containsKey("name"))    user.setName(updates.get("name"));
        if (updates.containsKey("surname")) user.setSurname(updates.get("surname"));
        if (updates.containsKey("region"))  user.setRegion(updates.get("region"));
        if (updates.containsKey("phone"))   user.setPhone(updates.get("phone"));

        userRepository.save(user);

        return ResponseEntity.ok(Map.of("message", "Profile updated successfully"));
    }
}