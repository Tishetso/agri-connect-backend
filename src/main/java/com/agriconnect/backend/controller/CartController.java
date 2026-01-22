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
    public ResponseEntity<?> addToCart(  // ← FIXED: was addToCard
                                         @RequestBody Map<String, Object> payload,
                                         Authentication auth){
        try{
            // Add logging to debug
            System.out.println("Received payload: " + payload);

            // Check if required fields exist
            if (payload.get("listingId") == null || payload.get("quantity") == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "listingId and quantity are required"));
            }

            Long listingId = Long.valueOf(payload.get("listingId").toString());
            Integer quantity = Integer.valueOf(payload.get("quantity").toString());
            String email = auth.getName();

            System.out.println("Adding to cart - listingId: " + listingId + ", quantity: " + quantity + ", email: " + email);

            CartDTO cart = cartService.addToCart(email, listingId, quantity);
            return ResponseEntity.ok(cart);

        } catch(NumberFormatException e){
            System.err.println("Invalid number format: " + e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid listingId or quantity format"));
        } catch(Exception e){
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
    @PutMapping("/item/{itemId}")  // ← ADDED: was missing annotation
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

    /*Delete /api/cart/item/{itemId} - Removes an item from the cart*/
    @DeleteMapping("/item/{itemId}")
    public ResponseEntity<?> removeCartItem(@PathVariable Long itemId){
        try{
            cartService.removeCartItem(itemId);
            return ResponseEntity.ok(Map.of("message", "Item removed from cart"));

        }catch(Exception e){
            System.err.println("Error removing cart item: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /*Delete /api/cart/{cartId} - Clear entire cart*/
    @DeleteMapping("/{cartId}")
    public ResponseEntity<?> clearCart(@PathVariable Long cartId){
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