package com.agriconnect.backend.service;

import com.agriconnect.backend.dto.CartDTO;
import com.agriconnect.backend.dto.CartItemDTO;
import com.agriconnect.backend.model.Cart;
import com.agriconnect.backend.model.CartItem;
import com.agriconnect.backend.model.Listing;
import com.agriconnect.backend.model.User;
import com.agriconnect.backend.repository.CartItemRepository;
import com.agriconnect.backend.repository.CartRepository;
import com.agriconnect.backend.repository.ListingRepository;
import com.agriconnect.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ListingRepository listingRepository;

    @Autowired
    private UserRepository userRepository;

    /*Add item to Cart*/
    @Transactional
    public CartDTO addToCart(String consumerEmail, Long listingId, Integer quantity){
        User consumer = userRepository.findByEmail(consumerEmail)
                .orElseThrow(() -> new RuntimeException("Consumer not found"));

        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new RuntimeException("Listing not found"));

        User farmer = listing.getUser();

        //Get or create cart for this farmer
        Cart cart = cartRepository.findByConsumerIdAndFarmerId(consumer.getId(), farmer.getId())
                .orElseGet(() -> {
                    Cart newCart = new Cart(consumer, farmer);
                    return cartRepository.save(newCart);
                });

        //Check if item already in cart
        Optional<CartItem> existingItem = cartItemRepository.findByCartIdAndListingId(cart.getId(), listingId);

        if(existingItem.isPresent()){
            //update quantity
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
            cartItemRepository.save(item);
        }else{
            CartItem newItem = new CartItem(cart,listing, quantity);
            cart.addItem(newItem);
            cartItemRepository.save(newItem);
        }

        cart = cartRepository.save(cart);
        return convertToDTO(cart);
    }

    /*Gets All carts for consumer*/
    public List<CartDTO> getConsumerCarts(String email){
        List<Cart> carts = cartRepository.findByConsumerEmail(email);
        return carts.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /*Update the cart item quantity*/

    @Transactional
    public CartDTO updateCartItemQuantity(Long cartItemId, Integer quantity){
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        if (quantity <= 0 ){
            //Remove Item if quantity is )
            Cart cart = item.getCart();
            cart.removeItem(item);
            cartItemRepository.delete(item);
            return convertToDTO(cart);
        }else{
            item.setQuantity(quantity);;
            cartItemRepository.save(item);
            return convertToDTO (item.getCart());
        }
    }

    /*Remove Item from cart*/

    @Transactional
    public void removeCartItem(Long cartItemId){
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        Cart cart = item.getCart();
        cart.removeItem(item);
        cartItemRepository.delete(item);

        //If cart is empty, delete it
        if (cart.getItems().isEmpty()){
            cartRepository.delete(cart);
        }

    }

    /*Clear entire cart*/
    @Transactional
    public void clearCart(Long cartId){
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
        cartRepository.delete(cart);
    }

    /*Convert cart entity to DTO*/
    private CartDTO convertToDTO(Cart cart){
        String farmerName = cart.getFarmer().getName() + " " + cart.getFarmer().getSurname();
        String farmerRegion = cart.getFarmer().getRegion();

        List<CartItemDTO> itemDTOs = cart.getItems().stream()
                .map(item -> new CartItemDTO(
                        item.getId(),
                        item.getListing().getId(),
                        item.getListing().getProduct(),
                        item.getListing().getPrice(),
                        item.getQuantity(),
                        item.getSubtotal(),
                        item.getListing().getImageUrls()
                ))
                .collect(Collectors.toList());

        Integer totalItems = cart.getItems().stream()
                .mapToInt(CartItem::getQuantity)
                .sum();

        return new CartDTO(
                cart.getId(),
                farmerName,
                farmerRegion,
                cart.getFarmer().getId(),
                itemDTOs,
                cart.getTotalPrice(),
                totalItems
        );


    }


}
