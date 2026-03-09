package com.agriconnect.backend.repository;

import com.agriconnect.backend.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    //Find orders by Consumer
    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.items WHERE o.consumer.id = :consumerId ORDER BY o.createdAt DESC")
    List<Order> findByConsumerId(@Param("consumerId") Long consumerId);

    //Find orders by consumer email
    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.items WHERE o.consumer.email = :email ORDER BY o.createdAt DESC")
    List<Order> findByConsumerEmail(@Param("email") String email);

    //Find orders by farmerID
    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.items WHERE o.farmer.id = :farmerId ORDER BY o.createdAt DESC")
    List<Order> findByFarmerId(@Param("farmerId") Long farmerId);

    //Find orders by farmerEmail
    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.items WHERE o.farmer.email = :email ORDER BY o.createdAt DESC")
    List<Order> findByFarmerEmail(@Param("email") String email);

    //Find pending orders for farmer
    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.items WHERE o.farmer.email = :email AND o.status = 'Pending' ORDER BY o.createdAt DESC")
    List<Order> findPendingOrdersByFarmerEmail(@Param("email") String email);

    // ✅ ADD DRIVER-RELATED QUERIES

    // Find orders by delivery status with no driver assigned
    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.items WHERE o.deliveryStatus = :status AND o.driver IS NULL ORDER BY o.createdAt ASC")
    List<Order> findByDeliveryStatusAndDriverIdIsNull(@Param("status") String status);

    // Find orders by driver ID
    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.items WHERE o.driver.id = :driverId ORDER BY o.createdAt DESC")
    List<Order> findByDriverId(@Param("driverId") Long driverId);

    // Find orders by driver ID and delivery status
    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.items WHERE o.driver.id = :driverId AND o.deliveryStatus = :status ORDER BY o.createdAt DESC")
    List<Order> findByDriverIdAndDeliveryStatus(@Param("driverId") Long driverId, @Param("status") String status);
}