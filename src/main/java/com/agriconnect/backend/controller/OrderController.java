package com.agriconnect.backend.controller;

import com.agriconnect.backend.dto.CreateOrderRequest;
import com.agriconnect.backend.dto.OrderDTO;
import com.agriconnect.backend.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /*POST - creating order*/
    @PostMapping("/create")
    public ResponseEntity<?> createOrder(
            @RequestBody CreateOrderRequest request,
            Authentication auth){
        try{
            String email = auth.getName();
            System.out.println("Creating order for: " + email);
            System.out.println("Cart ID: " + request.getCartId());

            OrderDTO order = orderService.createOrderFromCart(email, request);

            return ResponseEntity.ok(order);

        }catch(Exception e){
            System.err.println("Error creating order: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /*GET all orders for logged-in user*/
    @GetMapping
    public ResponseEntity<?> getOrders(Authentication auth){
        try{
            String email = auth.getName();

            //Get user from database to check role
            //For now, well return consumer orders
            //Todo: Check user role and return appropriate orders

            List<OrderDTO> orders = orderService.getConsumerOrders(email);

            return ResponseEntity.ok(orders);
        }catch(Exception e){
            System.err.println("Error fetching orders: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }

    }

    /*GET orders for farmer*/
    @GetMapping("/farmer")
    public ResponseEntity<?> getFarmerOrders(Authentication auth){
        try{
            String email = auth.getName();
            List<OrderDTO> orders = orderService.getFarmerOrders(email);
            return ResponseEntity.ok(orders);

        }catch(Exception e){
            System.err.println("Error fetching farmer orders: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /*GET Pending orders for farmer*/
    @GetMapping("/farmer/pending")
    public ResponseEntity<?> getFarmerPendingOrders(Authentication auth){
        try{
            String email = auth.getName();
            List<OrderDTO> orders = orderService.getFarmerPendingOrders(email);
            return ResponseEntity.ok(orders);

        }catch (Exception e){
            System.err.println("Error fetching pending orders: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /*GET order ny ID*/
    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable Long id){
        try{
            OrderDTO order = orderService.getOrderById(id);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            System.err.println("Error fetching order: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /*PUT - Confirm Order Farmer*/
    @PutMapping("/{id}/confirm")
    public ResponseEntity<?> confirmOrder(
            @PathVariable Long id,
            Authentication auth){
        try{
            String email = auth.getName();
            OrderDTO order = orderService.confirmOrder(id,email);
            return ResponseEntity.ok(order);

        }catch (Exception e){
            System.err.println("Error confirming order: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /*PUT - Update order Status*/
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> payload,
            Authentication auth) {

        try {
            String email = auth.getName();
            String status = payload.get("status");

            OrderDTO order = orderService.updateOrderStatus(id, status, email);

            return ResponseEntity.ok(order);

        }catch(Exception e){
            System.err.println("Error updating the status of the order: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }

    }

    /*DELETE  - Cancel order*/
    @DeleteMapping("/{id}/cancel")
    public ResponseEntity<?> cancelOrder (
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> payload,
            Authentication auth){
        try{
            String email = auth.getName();
            String reason = payload != null ? payload.get("reason") : "No reason provided";

            OrderDTO order = orderService.cancelOrder(id,email,reason);

            return ResponseEntity.ok(order);

        }catch(Exception e){
            System.err.println("Error cancelling order");
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }


}
