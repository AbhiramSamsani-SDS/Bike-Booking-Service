package com.bikebookingservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.bikebookingservice.entity.DriverTransactions;

public interface DriverTransactionRepository extends JpaRepository<DriverTransactions, Integer> {
    Page<DriverTransactions> findByDriver_DriverId(int driverId, Pageable pageable);
}
