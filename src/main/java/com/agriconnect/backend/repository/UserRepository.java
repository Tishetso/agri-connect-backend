package com.agriconnect.backend.repository;

import com.agriconnect.backend.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByResetToken(String token);

    boolean existsByEmail(String email);

    long countByRole(String role);

    // ADD THIS — used by GET /api/admin/users
    @Query("""
        SELECT u FROM User u
        WHERE (:role   IS NULL OR LOWER(u.role)   = LOWER(:role))
          AND (:status IS NULL OR LOWER(u.status) = LOWER(:status))
          AND (:search IS NULL
               OR LOWER(u.name)  LIKE LOWER(CONCAT('%', :search, '%'))
               OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')))
        """)
    Page<User> findWithFilters(
            @Param("role")   String role,
            @Param("status") String status,
            @Param("search") String search,
            Pageable pageable
    );
}
