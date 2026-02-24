package com.agriconnect.backend.controller;


import com.agriconnect.backend.model.Driver;
import com.agriconnect.backend.model.Order;
import com.agriconnect.backend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/driver")
public class DriverController {
    @Autowired
    private DriverService driverService;

    @Autowired
    private JwtUtil jwtUtil;

    //Register as driver
    @PostMapping("/register")
    public ResponseEntity<?> registerDriver(
            @RequestBody Driver driver,
            @RequestHeader("Authorization") String token) {
        try {
            String email = jwtUtil.extractEmail(token.substring(7));
            Driver registered = driverService.registerDriver(email, driver);
            return ResponseEntity.ok(registered);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }

    }

    //Get driver profile
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@RequestHeader("Authorization") String token) {
        try {
            String email = jwtUtil.extractEmail(token.substring(7));
            Driver driver = driverService.getDriverEmail(email);
            return ResponseEntity.ok(driver);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    //Toggle Availability
    @PutMapping("/availability")
    public ResponseEntity<?> toggleAvailability(
            @RequestHeader("Authorization") String token,
            @RequestBody Map<String, Boolean> request) {
        try {
            String email = jwtUtil.extractEmail(token.substring(7));
            Boolean available = request.get("available");
            Driver driver = driverService.updateAvailability(email, available);
            return ResponseEntity.ok(driver);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    //Get available orders for delivery
    @GetMapping("/available-orders")
    public ResponseEntity<?> getAvailableOrders(
            @RequestHeader("Authorization") String token) {
        try {
            List<Order> orders = driverService.getAvailableOrders();
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    //Accept delivery order
    @PostMapping("/accept/{orderId")
    public ResponseEntity<?> acceptOrder(
            @PathVariable Long orderId,
            @RequestHeader("Authorization") String token) {
        try {
            String email = jwtUtil.extractEmail(token.substring(7));
            Order order = driverService.acceptOrder(email, orderId);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    //Get driver's assigned orders
    @GetMapping("/my-deliveries")
    public ResponseEntity<?> getMyDeliveries(
            @RequestHeader("Authorization") String token) {
        try {
            String email = jwtUtil.extractEmail(token.substring(7));
            List<Order> orders = driverService.getDriverDeliveries(email);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    //Update delivery status
    @PutMapping("/update-status/{orderId")
    public ResponseEntity<?> updateDeliveryStatus(
            @PathVariable Long orderId,
            @RequestHeader("Authorization") String token,
            @RequestBody Map<String, String> request) {
        try{
            String email = jwtUtil.extractEmail(token.substring(7));
            String status = request.get("status");
            Order order = driverService.updateDeliveryStatus(email, orderId, status);
            return ResponseEntity.ok(order);
        }catch(Exception e){
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    //Get earnings summary
    @GetMapping("/earnings")
    public ResponseEntity<?> getEarnings(
            @RequestHeader("Authorization") String token){
        try{
            String email = jwtUtil.extractEmail(token.substring(7));
            Map<String, Object> earnings = driverService.getEarnings(email);
            return ResponseEntity.ok(earnings);
        }catch(Exception e){
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

}
