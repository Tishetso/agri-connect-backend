package com.agriconnect.backend.service;

import com.agriconnect.backend.dto.DriverLoginRequest;
import com.agriconnect.backend.dto.DriverRegistrationRequest;
import com.agriconnect.backend.model.Driver;
import com.agriconnect.backend.model.Order;
import com.agriconnect.backend.repository.DriverRepository;
import com.agriconnect.backend.repository.OrderRepository;
import com.agriconnect.backend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DriverService {
    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public Map<String, Object> loginDriver(DriverLoginRequest request) {
        Driver driver = driverRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("No account found with that email"));

        if (!passwordEncoder.matches(request.getPassword(), driver.getPassword())) {
            throw new RuntimeException("Incorrect password");
        }

        /*if (!Boolean.TRUE.equals(driver.getIsVerified())) {
            throw new RuntimeException("Your account is pending admin verification");
        }*/

        String token = jwtUtil.generateToken(driver.getEmail(), "DRIVER");

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("id", driver.getId());
        response.put("name", driver.getName());
        response.put("email", driver.getEmail());
        response.put("vehicleType", driver.getVehicleType());
        response.put("isAvailable", driver.getIsAvailable());
        response.put("kycSubmitted", driver.getKycSubmitted() != null && driver.getKycSubmitted());
        return response;
    }

    public Driver registerDriver(DriverRegistrationRequest request) {
        if (driverRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("A driver with this email already exists");
        }
        if (driverRepository.existsByIdNumber(request.getIdNumber())) {
            throw new RuntimeException("A driver with this ID number already exists");
        }

        Driver driver = new Driver();
        driver.setName(request.getName());
        driver.setSurname(request.getSurname());
        driver.setIdNumber(request.getIdNumber());
        driver.setEmail(request.getEmail());
        driver.setPhoneNumber(request.getPhoneNumber());
        driver.setAddress(request.getAddress());
        driver.setLatitude(request.getLatitude());
        driver.setLongitude(request.getLongitude());
        driver.setVehicleType(request.getVehicleType());

        if ("bicycle".equalsIgnoreCase(request.getVehicleType())) {
            driver.setLicenseNumber("N/A");
            driver.setVehicleRegistration("BICYCLE");
        } else {
            driver.setLicenseNumber(request.getLicenseNumber());
            driver.setVehicleRegistration(request.getVehicleRegistration());
        }

        driver.setIsVerified(false);
        driver.setIsAvailable(false);
        driver.setRating(0.0);
        driver.setTotalDeliveries(0);
        driver.setCreatedAt(LocalDateTime.now());
        driver.setPassword(passwordEncoder.encode(request.getPassword()));

        return driverRepository.save(driver);
    }

    // ✅ Fixed: query Driver directly by email instead of going through User
    public Driver getDriverByEmail(String email) {
        return driverRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Driver not found"));
    }

    public Driver updateAvailability(String email, Boolean available) {
        Driver driver = getDriverByEmail(email);
        driver.setIsAvailable(available);
        return driverRepository.save(driver);
    }

    @Transactional
    public List<Order> getAvailableOrders() {
        return orderRepository.findByDeliveryStatusAndDriverIdIsNull("PENDING");
    }

    public Order acceptOrder(String email, long orderId) {
        Driver driver = getDriverByEmail(email);

        if (!driver.getIsAvailable()) {
            throw new RuntimeException("You must be available to accept orders");
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getDriverId() != null) {
            throw new RuntimeException("Order already assigned to another driver");
        }

        order.setDriver(driver);
        order.setDeliveryStatus("ASSIGNED");

        return orderRepository.save(order);
    }

    @Transactional
    public List<Order> getDriverDeliveries(String email) {
        Driver driver = getDriverByEmail(email);
        return orderRepository.findByDriverId(driver.getId());
    }

    @Transactional
    public Order updateDeliveryStatus(String email, Long orderId, String status) {
        Driver driver = getDriverByEmail(email);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getDriverId().equals(driver.getId())) {
            throw new RuntimeException("This order is not assigned to you");
        }

        order.setDeliveryStatus(status);

        if (status.equals("PICKED_UP")) {
            order.setPickupTime(LocalDateTime.now());
        } else if (status.equals("DELIVERED")) {
            order.setDeliveryTime(LocalDateTime.now());
            order.setStatus("Delivered");
            driver.setTotalDeliveries(driver.getTotalDeliveries() + 1);
            driverRepository.save(driver);
        }

        return orderRepository.save(order);
    }

    public Map<String, Object> getEarnings(String email) {
        Driver driver = getDriverByEmail(email);

        List<Order> deliveredOrders = orderRepository
                .findByDriverIdAndDeliveryStatus(driver.getId(), "DELIVERED");

        double totalEarnings = deliveredOrders.stream()
                .mapToDouble(Order::getDeliveryFee)
                .sum();

        Map<String, Object> earnings = new HashMap<>();
        earnings.put("totalEarnings", totalEarnings);
        earnings.put("totalDeliveries", driver.getTotalDeliveries());
        earnings.put("rating", driver.getRating());
        earnings.put("deliveries", deliveredOrders);

        return earnings;
    }
}