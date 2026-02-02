package com.agriconnect.backend.service;

import com.agriconnect.backend.dto.CreateOrderRequest;
import com.agriconnect.backend.dto.OrderDTO;
import com.agriconnect.backend.dto.OrderItemDTO;
import com.agriconnect.backend.model.Cart;
import com.agriconnect.backend.model.CartItem;
import com.agriconnect.backend.model.Order;
import com.agriconnect.backend.model.OrderItem;
import com.agriconnect.backend.repository.CartRepository;
import com.agriconnect.backend.repository.OrderRepository;
import com.agriconnect.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationService notificationService;

    /*Create order From cart*/

    @Transactional
    public OrderDTO createOrderFromCart(String consumerEmail, CreateOrderRequest request) {
        //Get cart
        Cart cart = cartRepository.findById(request.getCartId())
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        //verify cart belongs to consumer
        if (!cart.getConsumer().getEmail().equals(consumerEmail)) {
            throw new RuntimeException("Unauthorized access to cart");
        }

        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        //Create order
        Order order = new Order(
                cart.getConsumer(),
                cart.getFarmer(),
                request.getDeliveryAddress(),
                request.getContactNumber(),
                request.getDeliveryNotes(),
                request.getPaymentMethod(),
                cart.getTotalPrice()
        );

        //Copy cart items to order items (Snapshot Data)
        for (CartItem cartItem : cart.getItems()) {
            OrderItem orderItem = new OrderItem(
                    order,
                    cartItem.getListing(),
                    cartItem.getListing().getProduct(),
                    cartItem.getQuantity(),
                    cartItem.getListing().getPrice(),
                    cartItem.getSubtotal()
            );
            order.addItem(orderItem);
        }

        //Save order
        Order savedOrder = orderRepository.save(order);

        //Delete Cart
        cartRepository.delete(cart);

        //Send notifications

        notificationService.notifyFarmerNewOrder(savedOrder);
        notificationService.notifyConsumerOrderPlaced(savedOrder);

        return convertToDTO(savedOrder);
    }

    /*Get all orders for consumer*/
    public List<OrderDTO> getConsumerOrders(String email) {
        List<Order> orders = orderRepository.findByConsumerEmail(email);
        return orders.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /*Get orders for farmer*/
    public List<OrderDTO> getFarmerOrders(String email) {
        List<Order> orders = orderRepository.findByFarmerEmail(email);
        return orders.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /*Get pending orders for farmer*/
    public List<OrderDTO> getFarmerPendingOrders(String email) {
        List<Order> orders = orderRepository.findPendingOrdersByFarmerEmail(email);
        return orders.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /*Confirm order (Farmer action)*/
    @Transactional
    public OrderDTO confirmOrder(Long orderId, String farmerEmail) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        //Verify order belongs to farmer
        if (!order.getFarmer().getEmail().equals(farmerEmail)) {
            throw new RuntimeException("Unauthorized access to order");
        }

        if (!order.getStatus().equals("Pending")) {
            throw new RuntimeException("Order is not pending");
        }

        order.setStatus("Confirmed");
        order.setConfirmedAt(LocalDateTime.now());

        Order updated = orderRepository.save(order);

        //Notify consumer
        notificationService.notifyConsumerOrderConfirmed(updated);

        return convertToDTO(updated);
    }

    /*Update order status**/
    @Transactional
    public OrderDTO updateOrderStatus(Long orderId, String status, String userEmail) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        //Verify user is the farmer
        if (!order.getFarmer().getEmail().equals(userEmail)) {
            throw new RuntimeException("Unauthorized");
        }

        String oldStatus = order.getStatus();
        order.setStatus(status);

        if (status.equals("Delivered")) {
            order.setDeliveredAt(LocalDateTime.now());
            //Notify the user
            notificationService.notifyConsumerOrderDelivered(order);
        }

        return convertToDTO(orderRepository.save(order));

    }


    /*Cancel order(consumer action*/
    @Transactional
    public OrderDTO cancelOrder(Long orderId, String consumerEmail, String reason) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order id not found"));

        //verify order belongs to consumer
        if (!order.getConsumer().getEmail().equals(consumerEmail)) {
            throw new RuntimeException("Unauthorized");
        }

        if (!order.getStatus().equals("Pending")) {
            throw new RuntimeException("Only pending orders can be cancelled");
        }

        order.setStatus("Cancelled");
        order.setCancellationReason(reason);

        Order updated = orderRepository.save(order);

        //Notify farmer
        notificationService.notifyFarmerOrderCancelled(updated);

        return convertToDTO(updated);
    }

    /*Get order by ID*/

    public OrderDTO getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        return convertToDTO(order);
    }

    /*Convert Order entity to DTO*/

    private OrderDTO convertToDTO(Order order) {
        String farmerName = order.getFarmer().getName() + " " + order.getFarmer().getSurname();
        String consumerName = order.getConsumer().getName() + " " + order.getConsumer().getSurname();

        List<OrderItemDTO> itemDTOs = order.getItems().stream()
                .map(item -> new OrderItemDTO(
                        item.getId(),
                        item.getProductName(),
                        item.getQuantity(),
                        item.getPricePerUnit(),
                        item.getSubtotal()
                ))
                .collect(Collectors.toList());

        return new OrderDTO(
                order.getId(),
                farmerName,
                order.getFarmer().getRegion(),
                order.getFarmer().getId(),
                consumerName,
                order.getConsumer().getId(),
                itemDTOs,
                order.getDeliveryAddress(),
                order.getContactNumber(),
                order.getDeliveryNotes(),
                order.getPaymentMethod(),
                order.getPaymentStatus(),
                order.getTotalPrice(),
                order.getDeliveryFee(),
                order.getGrandTotal(),
                order.getStatus(),
                order.getCancellationReason(),
                order.getCreatedAt(),
                order.getConfirmedAt(),
                order.getDeliveredAt()
        );


    }


}






















