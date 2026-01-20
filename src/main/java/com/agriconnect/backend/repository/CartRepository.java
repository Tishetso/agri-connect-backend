package com.agriconnect.backend.repository;

import com.agriconnect.backend.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    //Find all carts for a consumer
    @Query("SELECT c FROM Cart c LEFT JOIN FETCH c.items WHERE c.consumer.id = :consumerId")
    List<Cart> findByConsumerId(@Param("consumerId") Long consumerId) ;

    //Find specific cart for consumer and farmer
    @Query("SELECT c FROM Cart c LEFT JOIN FETCH c.items WHERE c.consumer.id = :consumerId AND c.farmer.id = :farmerId")
    Optional<Cart> findByConsumerIdAndFarmerId(@Param("consumerId") Long consumerId, @Param("farmerId") Long farmerId);

    //Find Cart by consumer email
    @Query("SELECT c FROM Cart c LEFT JOIN FETCH c.items WHERE c.consumer.email = :email")
    List<Cart> findByConsumerEmail(@Param("email") String email);

}
