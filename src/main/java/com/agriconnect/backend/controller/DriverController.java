package com.agriconnect.backend.controller;


import com.agriconnect.backend.dto.DriverLoginRequest;
import com.agriconnect.backend.dto.DriverRegistrationRequest;
import com.agriconnect.backend.model.Driver;
import com.agriconnect.backend.model.Order;
import com.agriconnect.backend.repository.DriverRepository;
import com.agriconnect.backend.repository.OrderRepository;
import com.agriconnect.backend.service.DriverService;
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
    @Autowired
    private DriverRepository driverRepository;
    @Autowired
    private OrderRepository orderRepository;

    //Register as driver
    @PostMapping("/register")
    public ResponseEntity<?> registerDriver(@RequestBody DriverRegistrationRequest request) {
        try {
            Driver registered = driverService.registerDriver(request);
            return ResponseEntity.ok(Map.of(
                    "message", "Registration submitted successfully",
                    "driverId", registered.getId()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Driver login
    @PostMapping("/login")
    public ResponseEntity<?> loginDriver(@RequestBody DriverLoginRequest request) {
        try {
            Map<String, Object> response = driverService.loginDriver(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest() /*changed 403 to badRequest() 400*/
                    .body(Map.of("error", e.getMessage()));
        }
    }

    //Get driver profile
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@RequestHeader("Authorization") String token) {
        try {
            String email = jwtUtil.extractEmail(token.substring(7));
            Driver driver = driverService.getDriverByEmail(email);
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
    @PostMapping("/accept/{orderId}")
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
    @PutMapping("/update-status/{orderId}")
    public ResponseEntity<?> updateDeliveryStatus(
            @PathVariable Long orderId,
            @RequestHeader("Authorization") String token,
            @RequestBody Map<String, String> request) {
        try{
            String email = jwtUtil.extractEmail(token.substring(7));
            String status = request.get("status");
            driverService.updateDeliveryStatus(email, orderId, status);
            /*Order order = driverService.updateDeliveryStatus(email, orderId, status);*/
            return ResponseEntity.ok(Map.of(
                    "message", "Status updated",
                    "orderId", orderId,
                    "deliveryStatus", status
            ));
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

    /*Getting all drivers  List<Driver> findByIsAvailableTrue();*/
    @GetMapping("/available-drivers")
    public ResponseEntity<?> getAvailableDrivers(@RequestHeader("Authorization") String token) {
        try {
            List<Driver> drivers = driverRepository.findByIsAvailableTrue();
            return ResponseEntity.ok(drivers);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }


    @PostMapping("/assign/{orderId}")
    public ResponseEntity<?> assignDriverToOrder(
            @PathVariable Long orderId,
            @RequestHeader("Authorization") String token,
            @RequestBody Map<String, Long> request) {
        try {
            Long driverId = request.get("driverId");

            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new RuntimeException("Order not found"));

            Driver driver = driverRepository.findById(driverId)
                    .orElseThrow(() -> new RuntimeException("Driver not found"));

            order.setDriver(driver);
            order.setDeliveryStatus("ASSIGNED");
            orderRepository.save(order);

            return ResponseEntity.ok(Map.of("message", "Driver assigned successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    //Order-driver assignment
    @PostMapping("/accept-assignment/{orderId}")
    public ResponseEntity<?> acceptAssignment(
            @PathVariable Long orderId,
            @RequestHeader("Authorization") String token) {
        try {
            String email = jwtUtil.extractEmail(token.substring(7));
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new RuntimeException("Order not found"));

            order.setDeliveryStatus("ACCEPTED");
            orderRepository.save(order);

            return ResponseEntity.ok(Map.of(
                    "message", "Order accepted",
                    "orderId", orderId,
                    "deliveryStatus", "ACCEPTED"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/reject/{orderId}")
    public ResponseEntity<?> rejectAssignment(
            @PathVariable Long orderId,
            @RequestHeader("Authorization") String token) {
        try{
            String email = jwtUtil.extractEmail(token.substring(7));
            Driver driver = driverService.getDriverByEmail(email);

            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new RuntimeException("Order not found"));

            if (!driver.getId().equals(order.getDriverId())){
                throw new RuntimeException("This order is not assigned to you");
            }

            order.setDriver(null);
            order.setDeliveryStatus("PENDING");
            order.setStatus("Pending");
            orderRepository.save(order);

            return ResponseEntity.ok(Map.of("message", "Order rejected"));
        }catch(Exception e){
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
