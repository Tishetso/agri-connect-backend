package com.agriconnect.backend.service;

import com.agriconnect.backend.model.Driver;
import com.agriconnect.backend.model.Order;
import com.agriconnect.backend.model.User;
import com.agriconnect.backend.repository.DriverRepository;
import com.agriconnect.backend.repository.OrderRepository;
import com.agriconnect.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FarmerDriverService {

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Haversine formula — calculates distance in km between two lat/lng points
     */
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int EARTH_RADIUS_KM = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_KM * c;
    }

    /**
     * Get available drivers within radius of the farmer who owns the order
     */
    public List<Map<String, Object>> getNearbyDrivers(Long orderId, double radiusKm, String farmerEmail) {
        // 1. Get the order and verify it belongs to this farmer
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getFarmer().getEmail().equals(farmerEmail)) {
            throw new RuntimeException("Unauthorized: This order does not belong to you");
        }

        // 2. Get farmer coordinates from the order's farmer user
        User farmer = order.getFarmer();
        if (farmer.getLatitude() == null || farmer.getLongitude() == null) {
            throw new RuntimeException("Farmer location not set. Please update your profile with your location.");
        }

        double farmerLat = farmer.getLatitude();
        double farmerLon = farmer.getLongitude();

        // 3. Get all available drivers and filter by distance
        List<Driver> availableDrivers = driverRepository.findAvailableDrivers();

        return availableDrivers.stream()
                .filter(driver -> driver.getLatitude() != null && driver.getLongitude() != null)
                .map(driver -> {
                    double distance = calculateDistance(
                            farmerLat, farmerLon,
                            driver.getLatitude(), driver.getLongitude()
                    );
                    Map<String, Object> driverMap = new HashMap<>();
                    driverMap.put("id", driver.getId());
                    driverMap.put("name", driver.getName());
                    driverMap.put("surname", driver.getSurname());
                    driverMap.put("phoneNumber", driver.getPhoneNumber());
                    driverMap.put("vehicleType", driver.getVehicleType());
                    driverMap.put("vehicleRegistration", driver.getVehicleRegistration());
                    driverMap.put("rating", driver.getRating());
                    driverMap.put("distanceKm", Math.round(distance * 10.0) / 10.0);
                    return driverMap;
                })
                .filter(map -> (double) map.get("distanceKm") <= radiusKm)
                .sorted((a, b) -> Double.compare(
                        (double) a.get("distanceKm"),
                        (double) b.get("distanceKm")
                )) // nearest first
                .collect(Collectors.toList());
    }

    /**
     * Assign a driver to an order
     */
    public Order assignDriver(Long orderId, Long driverId, String farmerEmail) {
        // 1. Get and validate the order
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getFarmer().getEmail().equals(farmerEmail)) {
            throw new RuntimeException("Unauthorized: This order does not belong to you");
        }

        if (order.getDriver() != null) {
            throw new RuntimeException("Order already has a driver assigned");
        }

        // 2. Get and validate the driver
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        if (!driver.getIsAvailable()){
            throw new RuntimeException("Driver is not available");
        }

        // 3. Assign driver and update statuses
        order.setDriver(driver);
        order.setDeliveryStatus("ASSIGNED");
        order.setStatus("Confirmed"); // also confirm the order

        return orderRepository.save(order);
    }
}