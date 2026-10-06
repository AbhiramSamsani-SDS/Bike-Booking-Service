package com.bikebookingservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.bikebookingservice.entity.Rides;
import com.bikebookingservice.enums.RideStatus;

public interface RideRepository extends JpaRepository<Rides, Integer> {
    Page<Rides> findByUser_UserId(int userId, Pageable pageable);
    Page<Rides> findByDriver_DriverId(int driverId, Pageable pageable);
    long countByUser_UserId(int userId);
    long countByDriver_DriverId(int driverId);
    boolean existsByUser_UserIdAndRideStatus(int userId, RideStatus rideStatus);
    boolean existsByDriver_DriverIdAndRideStatus(int driverId, RideStatus rideStatus);
}
