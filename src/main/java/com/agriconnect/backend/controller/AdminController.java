package com.agriconnect.backend.controller;

import com.agriconnect.backend.model.Driver;
import com.agriconnect.backend.model.User;
import com.agriconnect.backend.repository.DriverRepository;
import com.agriconnect.backend.repository.OrderRepository;
import com.agriconnect.backend.repository.UserRepository;
import com.agriconnect.backend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired private DriverRepository driverRepository;
    @Autowired private UserRepository   userRepository;
    @Autowired private OrderRepository  orderRepository;
    @Autowired private JwtUtil          jwtUtil;

    // ─── Dashboard Stats ────────────────────────────────────────────────────

    @GetMapping("/stats")
    public ResponseEntity<?> getStats(@RequestHeader("Authorization") String token) {
        try {
            long totalUsers     = userRepository.count();
            long totalFarmers   = userRepository.countByRole("farmer");
            long totalConsumers = userRepository.countByRole("consumer");
            long totalDrivers   = userRepository.countByRole("driver");
            long totalOrders    = orderRepository.count();
            long pendingDrivers = driverRepository.findByKycSubmittedTrueAndIsVerifiedFalse().size();
            long verifiedDrivers= driverRepository.findByIsVerifiedTrue().size();

            double totalRevenue = orderRepository.findAll().stream()
                    .mapToDouble(o -> o.getGrandTotal() != null ? o.getGrandTotal() : 0)
                    .sum();

            Map<String, Object> stats = new HashMap<>();
            stats.put("totalUsers",      totalUsers);
            stats.put("totalFarmers",    totalFarmers);
            stats.put("totalConsumers",  totalConsumers);
            stats.put("totalDrivers",    totalDrivers);
            stats.put("totalOrders",     totalOrders);
            stats.put("totalRevenue",    totalRevenue);
            stats.put("pendingDrivers",  pendingDrivers);
            stats.put("verifiedDrivers", verifiedDrivers);
            stats.put("totalListings",   0);
            stats.put("activeListings",  0);
            stats.put("pendingListings", 0);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/listings/recent")
    public ResponseEntity<?> getRecentListings(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/orders/recent")
    public ResponseEntity<?> getRecentOrders(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(List.of());
    }

    // ─── Users ──────────────────────────────────────────────────────────────

    @GetMapping("/users")
    public ResponseEntity<?> getUsers(
            @RequestHeader("Authorization") String token,
            @RequestParam(defaultValue = "0")  int    page,
            @RequestParam(defaultValue = "10") int    size,
            @RequestParam(required = false)    String role,
            @RequestParam(required = false)    String status,
            @RequestParam(required = false)    String search
    ) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            Page<User> usersPage = userRepository.findWithFilters(role, status, search, pageable);

            List<Map<String, Object>> content = usersPage.getContent().stream()
                    .map(this::toUserMap)
                    .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("content",        content);
            response.put("totalPages",      usersPage.getTotalPages());
            response.put("totalElements",   usersPage.getTotalElements());
            response.put("totalUsers",      usersPage.getTotalElements());
            response.put("totalFarmers",    userRepository.countByRole("farmer"));
            response.put("totalConsumers",  userRepository.countByRole("consumer"));
            response.put("totalDrivers",    userRepository.countByRole("driver"));
            response.put("totalAdmins",     userRepository.countByRole("admin"));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/users/{id}/suspend")
    public ResponseEntity<?> suspendUser(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token
    ) {
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            user.setStatus("SUSPENDED");
            userRepository.save(user);
            return ResponseEntity.ok(Map.of("message", "User suspended"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/users/{id}/unsuspend")
    public ResponseEntity<?> unsuspendUser(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token
    ) {
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            user.setStatus("ACTIVE");
            userRepository.save(user);
            return ResponseEntity.ok(Map.of("message", "User unsuspended"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token
    ) {
        try {
            if (!userRepository.existsById(id)) return ResponseEntity.notFound().build();
            userRepository.deleteById(id);
            return ResponseEntity.ok(Map.of("message", "User deleted"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ─── Drivers ────────────────────────────────────────────────────────────

    @GetMapping("/drivers/pending")
    public ResponseEntity<?> getPendingDrivers(@RequestHeader("Authorization") String token) {
        try {
            return ResponseEntity.ok(driverRepository.findByKycSubmittedTrueAndIsVerifiedFalse());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/drivers")
    public ResponseEntity<?> getAllDrivers(
            @RequestHeader("Authorization") String token,
            @RequestParam(defaultValue = "0")  int    page,
            @RequestParam(defaultValue = "10") int    size,
            @RequestParam(required = false)    String verificationStatus,
            @RequestParam(required = false)    String search
    ) {
        try {
            List<Driver> allDrivers = driverRepository.findAll();

            // Filter by verificationStatus
            List<Driver> filtered = allDrivers.stream()
                    .filter(d -> {
                        if (verificationStatus == null) return true;
                        return verificationStatus.equals(resolveVerificationStatus(d));
                    })
                    .filter(d -> {
                        if (search == null || search.isBlank()) return true;
                        String s     = search.toLowerCase();
                        String name  = d.getUser() != null ? d.getUser().getName()  : "";
                        String email = d.getUser() != null ? d.getUser().getEmail() : "";
                        String lic   = d.getLicenseNumber() != null ? d.getLicenseNumber() : "";
                        return name.toLowerCase().contains(s)
                                || email.toLowerCase().contains(s)
                                || lic.toLowerCase().contains(s);
                    })
                    .collect(Collectors.toList());

            // Manual pagination
            int total      = filtered.size();
            int totalPages = (int) Math.ceil((double) total / size);
            int from       = Math.min(page * size, total);
            int to         = Math.min(from + size, total);

            List<Map<String, Object>> content = filtered.subList(from, to).stream()
                    .map(this::toDriverMap)
                    .collect(Collectors.toList());

            long pendingCount  = allDrivers.stream().filter(d -> "PENDING".equals(resolveVerificationStatus(d))).count();
            long verifiedCount = allDrivers.stream().filter(d -> "VERIFIED".equals(resolveVerificationStatus(d))).count();
            long rejectedCount = allDrivers.stream().filter(d -> "REJECTED".equals(resolveVerificationStatus(d))).count();

            Map<String, Object> response = new HashMap<>();
            response.put("content",        content);
            response.put("totalPages",      totalPages);
            response.put("totalElements",   total);
            response.put("totalDrivers",    allDrivers.size());
            response.put("pendingDrivers",  pendingCount);
            response.put("verifiedDrivers", verifiedCount);
            response.put("rejectedDrivers", rejectedCount);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/drivers/{driverId}/approve")
    public ResponseEntity<?> approveDriver(
            @PathVariable Long driverId,
            @RequestHeader("Authorization") String token
    ) {
        try {
            Driver driver = driverRepository.findById(driverId)
                    .orElseThrow(() -> new RuntimeException("Driver not found"));
            driver.setIsVerified(true);
            driverRepository.save(driver);
            return ResponseEntity.ok(Map.of("message", "Driver approved successfully", "driverId", driverId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // /verify is an alias so the frontend can call either
    @PutMapping("/drivers/{driverId}/verify")
    public ResponseEntity<?> verifyDriver(
            @PathVariable Long driverId,
            @RequestHeader("Authorization") String token
    ) {
        return approveDriver(driverId, token);
    }

    @PutMapping("/drivers/{driverId}/reject")
    public ResponseEntity<?> rejectDriver(
            @PathVariable Long driverId,
            @RequestHeader("Authorization") String token,
            @RequestBody(required = false) Map<String, String> request
    ) {
        try {
            Driver driver = driverRepository.findById(driverId)
                    .orElseThrow(() -> new RuntimeException("Driver not found"));
            driver.setIsVerified(false);
            driver.setKycSubmitted(false);
            driver.setIdDocumentUrl(null);
            driver.setSelfieUrl(null);
            driver.setVehiclePhotoUrl(null);
            driver.setLicenseDiskUrl(null);
            driver.setDriversLicenseUrl(null);
            driverRepository.save(driver);
            return ResponseEntity.ok(Map.of("message", "Driver rejected, must resubmit documents", "driverId", driverId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/drivers/{driverId}/suspend")
    public ResponseEntity<?> suspendDriver(
            @PathVariable Long driverId,
            @RequestHeader("Authorization") String token
    ) {
        try {
            Driver driver = driverRepository.findById(driverId)
                    .orElseThrow(() -> new RuntimeException("Driver not found"));
            if (driver.getUser() != null) {
                driver.getUser().setStatus("SUSPENDED");
                userRepository.save(driver.getUser());
            }
            return ResponseEntity.ok(Map.of("message", "Driver suspended"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/drivers/{driverId}/unsuspend")
    public ResponseEntity<?> unsuspendDriver(
            @PathVariable Long driverId,
            @RequestHeader("Authorization") String token
    ) {
        try {
            Driver driver = driverRepository.findById(driverId)
                    .orElseThrow(() -> new RuntimeException("Driver not found"));
            if (driver.getUser() != null) {
                driver.getUser().setStatus("ACTIVE");
                userRepository.save(driver.getUser());
            }
            return ResponseEntity.ok(Map.of("message", "Driver unsuspended"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ─── Helpers ────────────────────────────────────────────────────────────

    private Map<String, Object> toUserMap(User u) {
        Map<String, Object> map = new HashMap<>();
        map.put("id",        u.getId());
        map.put("name",      u.getName());
        map.put("email",     u.getEmail());
        map.put("role",      u.getRole() != null ? u.getRole().toUpperCase() : "CONSUMER");
        map.put("status",    u.getStatus() != null ? u.getStatus().toUpperCase() : "ACTIVE");
        map.put("createdAt", u.getCreatedAt());
        return map;
    }

    private Map<String, Object> toDriverMap(Driver d) {
        Map<String, Object> map = new HashMap<>();
        map.put("id",          d.getId());
        map.put("name",        d.getUser() != null ? d.getUser().getName()  : "");
        map.put("email",       d.getUser() != null ? d.getUser().getEmail() : "");
        map.put("phone",       d.getUser() != null ? d.getPhoneNumber() : "");
        map.put("licenseNumber",     d.getLicenseNumber());
        map.put("vehicleType",       d.getVehicleType());
        map.put("verificationStatus", resolveVerificationStatus(d));
        map.put("status",      d.getUser() != null && "SUSPENDED".equals(d.getUser().getStatus())
                ? "SUSPENDED" : "ACTIVE");
        map.put("createdAt",   d.getCreatedAt());

        // Collect document URLs for the detail modal
        List<String> docs = new ArrayList<>();
        if (d.getIdDocumentUrl()     != null) docs.add(d.getIdDocumentUrl());
        if (d.getSelfieUrl()         != null) docs.add(d.getSelfieUrl());
        if (d.getVehiclePhotoUrl()   != null) docs.add(d.getVehiclePhotoUrl());
        if (d.getLicenseDiskUrl()    != null) docs.add(d.getLicenseDiskUrl());
        if (d.getDriversLicenseUrl() != null) docs.add(d.getDriversLicenseUrl());
        map.put("documents", docs);

        return map;
    }

    /** Derives a PENDING / VERIFIED / REJECTED string from the Driver's KYC flags */
    private String resolveVerificationStatus(Driver d) {
        if (Boolean.TRUE.equals(d.getIsVerified()))   return "VERIFIED";
        if (Boolean.TRUE.equals(d.getKycSubmitted())) return "PENDING";
        return "REJECTED";
    }
}