package com.agriconnect.backend.repository;

import com.agriconnect.backend.model.Driver;
import com.agriconnect.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {
    boolean existsByEmail(String email);
    boolean existsByIdNumber(String idNumber);
    Optional<Driver> findByUser(User user);
    Optional<Driver> findByEmail(String email);

    @Query("SELECT d FROM Driver d WHERE d.isAvailable = true AND d.isVerified = true")
    List<Driver> findAvailableDrivers();


}
