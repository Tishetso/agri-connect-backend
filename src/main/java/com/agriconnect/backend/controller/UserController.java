package com.agriconnect.backend.controller;

import com.agriconnect.backend.model.User;
import com.agriconnect.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    private static final String UPLOAD_DIR = "uploads/";

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Authentication authentication) {
        String email = authentication.getName();
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "User not found"));
        }

        User user = userOpt.get();
        return ResponseEntity.ok(buildUserResponse(user));
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

    /**
     * POST /api/users/avatar
     * Accepts a multipart image, saves to disk, stores filename in DB.
     * Returns the full avatar URL so the frontend can update localStorage immediately.
     */
    @PostMapping("/avatar")
    public ResponseEntity<?> uploadAvatar(Authentication authentication,
                                          @RequestParam("avatar") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "No file provided"));
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return ResponseEntity.badRequest().body(Map.of("error", "Only image files are allowed"));
        }

        String email = authentication.getName();
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "User not found"));
        }

        try {
            User user = userOpt.get();

            // Delete old avatar file from disk if one exists
            if (user.getAvatarUrl() != null) {
                Path oldFile = Paths.get(UPLOAD_DIR + user.getAvatarUrl());
                Files.deleteIfExists(oldFile);
            }

            // Save new file
            String filename = "avatar_" + user.getId() + "_" + System.currentTimeMillis()
                    + getExtension(file.getOriginalFilename());
            Path savePath = Paths.get(UPLOAD_DIR + filename);
            Files.createDirectories(savePath.getParent());
            Files.write(savePath, file.getBytes());

            user.setAvatarUrl(filename);
            userRepository.save(user);

            return ResponseEntity.ok(Map.of(
                    "message",   "Avatar uploaded successfully",
                    "avatarUrl", filename
            ));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to upload avatar: " + e.getMessage()));
        }
    }

    /**
     * DELETE /api/users/avatar
     * Removes avatar from disk and clears the DB field.
     */
    @DeleteMapping("/avatar")
    public ResponseEntity<?> removeAvatar(Authentication authentication) {
        String email = authentication.getName();
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "User not found"));
        }

        try {
            User user = userOpt.get();
            if (user.getAvatarUrl() != null) {
                Files.deleteIfExists(Paths.get(UPLOAD_DIR + user.getAvatarUrl()));
                user.setAvatarUrl(null);
                userRepository.save(user);
            }
            return ResponseEntity.ok(Map.of("message", "Avatar removed"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to remove avatar"));
        }
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return ".jpg";
        return filename.substring(filename.lastIndexOf('.'));
    }

    private Map<String, Object> buildUserResponse(User user) {
        return Map.of(
                "id",        user.getId(),
                "name",      user.getName(),
                "surname",   user.getSurname(),
                "email",     user.getEmail(),
                "role",      user.getRole(),
                "status",    user.getStatus(),
                "region",    user.getRegion()    != null ? user.getRegion()    : "",
                "phone",     user.getPhone()     != null ? user.getPhone()     : "",
                "avatarUrl", user.getAvatarUrl() != null ? user.getAvatarUrl() : "",
                "createdAt", user.getCreatedAt().toString()
        );
    }
}