package com.agriconnect.backend.controller;

import com.agriconnect.backend.model.Order;
import com.agriconnect.backend.service.FarmerDriverService;
import com.agriconnect.backend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/farmer")
public class FarmerDriverController {

    @Autowired
    private FarmerDriverService farmerDriverService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * GET /api/farmer/nearby-drivers?orderId=5&radius=20
     * Returns available drivers sorted by distance from farmer
     */
    @GetMapping("/nearby-drivers")
    public ResponseEntity<?> getNearbyDrivers(
            @RequestHeader("Authorization") String token,
            @RequestParam Long orderId,
            @RequestParam(defaultValue = "20") double radius) {
        try {
            String farmerEmail = jwtUtil.extractEmail(token.substring(7));
            List<Map<String, Object>> drivers = farmerDriverService.getNearbyDrivers(orderId, radius, farmerEmail);
            return ResponseEntity.ok(drivers);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * PUT /api/farmer/orders/{orderId}/assign-driver/{driverId}
     * Assigns a driver to an order
     */
    @PutMapping("/orders/{orderId}/assign-driver/{driverId}")
    public ResponseEntity<?> assignDriver(
            @RequestHeader("Authorization") String token,
            @PathVariable Long orderId,
            @PathVariable Long driverId) {
        try {
            String farmerEmail = jwtUtil.extractEmail(token.substring(7));
            Order order = farmerDriverService.assignDriver(orderId, driverId, farmerEmail);
            return ResponseEntity.ok(Map.of(
                    "message", "Driver assigned successfully",
                    "orderId", order.getId(),
                    "deliveryStatus", order.getDeliveryStatus()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
}