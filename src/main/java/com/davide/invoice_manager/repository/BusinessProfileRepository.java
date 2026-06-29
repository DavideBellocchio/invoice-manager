package com.davide.invoice_manager.repository;

import com.davide.invoice_manager.domain.BusinessProfile;
import com.davide.invoice_manager.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BusinessProfileRepository extends JpaRepository<BusinessProfile, Long> {

    @Query("SELECT b FROM BusinessProfile b WHERE b.user = :user")
    Optional<BusinessProfile> findByUser(@Param("user") User user);
}
