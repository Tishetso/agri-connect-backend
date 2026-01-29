package com.agriconnect.backend.service;

import com.agriconnect.backend.dto.CreateOrderRequest;
import com.agriconnect.backend.dto.OrderDTO;
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
    public OrderDTO createOrderFromCart(String consumerEmail, CreateOrderRequest request){
        //Get cart
        Cart cart = cartRepository.findById(request.getCartId())
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        //verify cart belongs to consumer
        if(!cart.getConsumer().getEmail().equals(consumerEmail)){
            throw new RuntimeException("Unauthorized access to cart");
        }

        if (cart.getItems().isEmpty()){
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
        for(CartItem cartItem : cart.getItems()){
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

        return convertToDTO(saveOrder);

    }

}
