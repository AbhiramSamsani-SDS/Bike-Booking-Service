package com.bikebookingservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.bikebookingservice.entity.Drivers;

@Repository
public interface DriverRepository extends JpaRepository<Drivers, Integer> {
    Drivers findFirstByAvailableStatusTrueAndActiveTrue();
    Drivers findByDriverIdAndPasswordAndActiveTrue(int driverId, String password);
    boolean existsByEmail(String email);
    long countByAvailableStatusTrueAndActiveTrue();
}
