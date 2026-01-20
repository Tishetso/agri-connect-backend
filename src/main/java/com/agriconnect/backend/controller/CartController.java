package com.agriconnect.backend.controller;

import com.agriconnect.backend.dto.CartDTO;
import com.agriconnect.backend.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    /*POST /api/cart/add - Add item to cart*/

    @PostMapping("/add")
    public ResponseEntity<?> addToCard(
            @RequestBody Map<String, Object> payload,
            Authentication auth){
        try{
            Long listingId = Long.valueOf(payload.get("listingId").toString());
            Integer quantity = Integer.valueOf(payload.get("quantity").toString());
            String email = auth.getName();

            CartDTO cart = cartService.addToCart(email,listingId, quantity);
            return ResponseEntity.ok(cart);

        }catch(Exception e){
            System.err.println("Error adding to cart: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /*GET /api/cart - Get all carts for logged-in consumer*/

    @GetMapping
    public ResponseEntity<?> getConsumerCarts(Authentication auth){
        try{
            String email = auth.getName();
            List<CartDTO> carts = cartService.getConsumerCarts(email);
            return ResponseEntity.ok(carts);

        }catch (Exception e){
            System.err.println("Error fetching carts: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /*PUT /api/cart/item/{itemId} - Update cart item quantity*/
    public ResponseEntity<?> updateCartItem(
            @PathVariable Long itemId,
            @RequestBody Map<String, Integer> payload){
        try{
            Integer quantity = payload.get("quantity");
            CartDTO cart = cartService.updateCartItemQuantity(itemId, quantity);
            return ResponseEntity.ok(cart);

        }catch (Exception e){
         System.err.println("Error updating cart item: " + e.getMessage());
         e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));

        }
    }

    /*Delete /api/cart/item/{item} - Removes an item from the cart*/
    @DeleteMapping("/item/{itemId}")
    public ResponseEntity<?> removeCartItem(@PathVariable Long itemId){
        try{
            cartService.removeCartItem(itemId);
            return ResponseEntity.ok(Map.of("Message", "Item removed from cart"));

        }catch(Exception e){
            System.err.println("Error removing cart item: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }


    /*Delete /api/cart/{cartId}*/
    @DeleteMapping("/{cartId}")
    public ResponseEntity <?> clearCart (@PathVariable Long cartId){
        try{
            cartService.clearCart(cartId);
            return ResponseEntity.ok(Map.of("message", "Cart cleared"));

        }catch(Exception e){
            System.err.println("Error clearing cart: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
