package com.agriconnect.backend.repository;

import com.agriconnect.backend.model.Listing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ListingRepository extends JpaRepository<Listing, Long> {
    List<Listing> findByUserEmail(String email);

    @Query("SELECT l FROM Listing l " +
    "JOIN FETCH l.user u " +
    "WHERE l.status = 'Available' " +
    "ORDER BY l.id DESC")
    List<Listing> findAllAvailableListingsWithUser();

}
