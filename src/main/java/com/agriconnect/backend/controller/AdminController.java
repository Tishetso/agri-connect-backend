package com.agriconnect.backend.controller;

import com.agriconnect.backend.model.Driver;
import com.agriconnect.backend.repository.DriverRepository;
import com.agriconnect.backend.repository.OrderRepository;
import com.agriconnect.backend.repository.UserRepository;
import com.agriconnect.backend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrderRepository orderRepository;

    @GetMapping("/stats")
    public ResponseEntity<?> getStats(@RequestHeader("Authorization") String token) {
        try {
            long totalUsers = userRepository.count();
            long totalFarmers = userRepository.countByRole("farmer");
            long totalConsumers = userRepository.countByRole("consumer");
            long totalOrders = orderRepository.count();
            long pendingDrivers = driverRepository.findByKycSubmittedTrueAndIsVerifiedFalse().size();
            long verifiedDrivers = driverRepository.findByIsVerifiedTrue().size();

            double totalRevenue = orderRepository.findAll().stream()
                    .mapToDouble(o -> o.getGrandTotal() != null ? o.getGrandTotal() : 0)
                    .sum();

            return ResponseEntity.ok(Map.of(
                    "totalUsers", totalUsers,
                    "totalFarmers", totalFarmers,
                    "totalConsumers", totalConsumers,
                    "totalOrders", totalOrders,
                    "totalRevenue", totalRevenue,
                    "pendingDrivers", pendingDrivers,
                    "verifiedDrivers", verifiedDrivers,
                    "totalListings", 0,
                    "activeListings", 0,
                    "pendingListings", 0
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/listings/recent")
    public ResponseEntity<?> getRecentListings(@RequestHeader("Authorization") String token) {
        try {
            // return empty list for now until you wire listing repo
            return ResponseEntity.ok(List.of());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/orders/recent")
    public ResponseEntity<?> getRecentOrders(@RequestHeader("Authorization") String token) {
        try {
            return ResponseEntity.ok(List.of());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Get all drivers pending KYC verification
    @GetMapping("/drivers/pending")
    public ResponseEntity<?> getPendingDrivers(
            @RequestHeader("Authorization") String token) {
        try {
            List<Driver> drivers = driverRepository
                    .findByKycSubmittedTrueAndIsVerifiedFalse();
            return ResponseEntity.ok(drivers);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Get all drivers
    @GetMapping("/drivers")
    public ResponseEntity<?> getAllDrivers(
            @RequestHeader("Authorization") String token) {
        try {
            List<Driver> drivers = driverRepository.findAll();
            return ResponseEntity.ok(drivers);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    //Just added 27 April 2026
   /* @GetMapping("/stats")
    public ResponseEntity<?> getStats(@RequestHeader("Authorization") String token) {
        try {
            long pendingDrivers = driverRepository.findByKycSubmittedTrueAndIsVerifiedFalse().size();
            long verifiedDrivers = driverRepository.findByIsVerifiedTrue().size();

            return ResponseEntity.ok(Map.of(
                    "pendingDrivers", pendingDrivers,
                    "verifiedDrivers", verifiedDrivers
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
*/
    // Approve driver
    @PutMapping("/drivers/{driverId}/approve")
    public ResponseEntity<?> approveDriver(
            @PathVariable Long driverId,
            @RequestHeader("Authorization") String token) {
        try {
            Driver driver = driverRepository.findById(driverId)
                    .orElseThrow(() -> new RuntimeException("Driver not found"));

            driver.setIsVerified(true);
            driverRepository.save(driver);

            return ResponseEntity.ok(Map.of(
                    "message", "Driver approved successfully",
                    "driverId", driverId
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Reject driver - resets KYC so they can resubmit
    @PutMapping("/drivers/{driverId}/reject")
    public ResponseEntity<?> rejectDriver(
            @PathVariable Long driverId,
            @RequestHeader("Authorization") String token,
            @RequestBody Map<String, String> request) {
        try {
            Driver driver = driverRepository.findById(driverId)
                    .orElseThrow(() -> new RuntimeException("Driver not found"));

            driver.setIsVerified(false);
            driver.setKycSubmitted(false); // force resubmission
            // clear documents so they upload fresh
            driver.setIdDocumentUrl(null);
            driver.setSelfieUrl(null);
            driver.setVehiclePhotoUrl(null);
            driver.setLicenseDiskUrl(null);
            driver.setDriversLicenseUrl(null);
            driverRepository.save(driver);

            return ResponseEntity.ok(Map.of(
                    "message", "Driver rejected, must resubmit documents",
                    "driverId", driverId
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
}