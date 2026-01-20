package com.agriconnect.backend.repository;

import com.agriconnect.backend.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    //Find specific item in cart by listing
    @Query("SELECT ci FROM CartItem ci WHERE ci.cart.id = :cartId AND ci.listing.id = :listingId")
    Optional<CartItem> findByCartIdAndListingId(@Param("cartId") Long cartId, @Param("listingId") Long listingId);

}
