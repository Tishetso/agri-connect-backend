package com.agriconnect.backend.service;

import com.agriconnect.backend.model.User;
import com.agriconnect.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    public String generateResetToken(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            String token = UUID.randomUUID().toString();
            user.setResetToken(token);
            user.setTokenExpiry(new Date(System.currentTimeMillis() + 3600000)); // 1 hour
            userRepository.save(user);

            emailService.sendResetEmail(email, token);
            return token;
        }
        throw new RuntimeException("Email not found");
    }

    public void resetPassword(String token, String newPassword) {
        Optional<User> userOpt = userRepository.findByResetToken(token);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.getTokenExpiry().after(new Date())) {
                user.setPassword(passwordEncoder.encode(newPassword));
                user.setResetToken(null);
                user.setTokenExpiry(null);
                userRepository.save(user);
            } else {
                throw new RuntimeException("Token expired");
            }
        } else {
            throw new RuntimeException("Invalid token");
        }
    }

}
