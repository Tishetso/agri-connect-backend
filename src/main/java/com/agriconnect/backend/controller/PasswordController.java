package com.agriconnect.backend.controller;

import com.agriconnect.backend.dto.PasswordResetDto;
import com.agriconnect.backend.dto.PasswordResetRequestDto;
import com.agriconnect.backend.service.PasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class PasswordController {

    @Autowired
    private PasswordService passwordService;

    @PostMapping("/request-reset")
    public ResponseEntity<?> requestReset(@RequestBody PasswordResetRequestDto dto) {
        try {
            String token = passwordService.generateResetToken(dto.getEmail());
            // TODO: Send token via email or frontend link
            return ResponseEntity.ok(Map.of("resetToken", token));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody PasswordResetDto dto) {
        try {
            passwordService.resetPassword(dto.getToken(), dto.getNewPassword());
            return ResponseEntity.ok("Password reset successful");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}
