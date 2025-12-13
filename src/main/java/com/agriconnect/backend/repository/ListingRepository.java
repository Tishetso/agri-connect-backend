package com.agriconnect.backend.repository;

import com.agriconnect.backend.model.Listing;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ListingRepository extends JpaRepository<Listing, Long> {
    List<Listing> findByUserEmail(String email);

}
