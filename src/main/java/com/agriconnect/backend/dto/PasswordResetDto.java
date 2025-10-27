package com.agriconnect.backend.dto;

import lombok.Data;

@Data
public class PasswordResetDto {
    private String token;
    private String newPassword;
}
