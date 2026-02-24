package com.agriconnect.backend.service;

import com.agriconnect.backend.model.Driver;
import com.agriconnect.backend.model.Order;
import com.agriconnect.backend.model.User;
import com.agriconnect.backend.repository.OrderRepository;
import com.agriconnect.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DriverService {
    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    public Driver registerDriver(String email, Driver driverData){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        //Check if already registered as driver
        if (driverRepository.findByUser(user).isPresent()){
            throw new RuntimeException("Already registered as driver");
        }

        Driver driver = new Driver();
        driver.setUser(user);
        driver.setVehicleType(driverData.getVehicleType());
        driver.setLicenseNumber(driverData.getLicenseNumber());
        driver.setVehicleRegistration(driverData.getVehicleRegistration());
        driver.setPhoneNumber(driverData.getPhoneNumber());
        driver.setIsVerified(false); // Admin will verify
        driver.setIsAvailable(false);

        return driverRepository.save(driver);
    }

    public Driver getDriverByEmail(String email){
        User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new RuntimeException("User not found"));

        return driverRepository.findByUser(user)
        .orElseThrow(() -> new RuntimeException("Driver profile not found"));
    }

    public Driver updateAvailability(String email, Boolean available){
        Driver driver = getDriverByEmail(email);
        driver.setIsAvailable(available);
        return driverRepository.save(driver);
    }

    public List<Order> getAvailableOrders(){
        //Get orders that are pid but not assigned to driver
        return orderRepository.findByDeliveryStatusAndDriverIdIsNull("PENDING");
    }

    public Order acceptOrder(String email, long orderId){
        Driver driver = getDriverByEmail(email);

        if(!driver.getIsAvailable()){
            throw new RuntimeException("You must be available to accept orders");
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getDriverId() != null) {
            throw new RuntimeException("Order already assigned to another driver");
        }

        order.setDriverId(driver.getId());
        order.setDeliveryStatus("ASSIGNED");

        return orderRepository.save(order);
    }

    public List<Order> getDriverDeliveries(String email){
        Driver driver = getDriverByEmail(email);
        return orderRepository.findByDriverId(driver.getId());
    }

    public Order updateDeliveryStatus(String email, Long orderId, String status){
        Driver driver = getDriverByEmail(email);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order no found"));

        if(!order.getDriverId().equals(driver.getId())){
            throw new RuntimeException("This order is not assigned to you");
        }

        order.setDeliveryStatus(status);

        if(status.equals("PICKED_UP")){
            order.setPickupTime(LocalDateTime.now());
        }else if(status.equals("DELIVERED")){
            order.setDeliveryTime(LocalDateTime.now());

            //update driver stats
            driver.setTotalDeliveries(driver.getTotalDeliveries() + 1);
            driverRepository.save(driver);
        }

        return orderRepository.save(order);
    }



    public Map<String, Object> getEarnings(String email){
        Driver driver = getDriverByEmail(email);

        //get all delivered orders for this driver
        List<Order> deliveredOrders = orderRepository
                .findByDriverIdAndDeliveryStatus(driver.getId(), "DELIVERED");

        double totalEarnings = deliveredOrders.stream()
                .mapToDouble(Order::getDeliveryFee)
                .sum();

        Map<String,Object> earnings = new HashMap<>();
        earnings.put("totalEarnings", totalEarnings);
        earnings.put("totalDeliveries", driver.getTotalDeliveries());
        earnings.put("rating", driver.getRating());
        earnings.put("deliveries", deliveredOrders);

        return earnings;
    }

}
