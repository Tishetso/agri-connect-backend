package com.agriconnect.backend.repository;

import com.agriconnect.backend.model.Listing;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ListingRepository extends JpaRepository<Listing, Long> {
}
