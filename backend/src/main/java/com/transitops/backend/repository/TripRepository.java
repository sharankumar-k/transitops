package com.transitops.backend.repository;

import com.transitops.backend.entity.Trip;
import com.transitops.backend.enums.TripStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TripRepository extends JpaRepository<Trip, Long> {
    
    long countByStatus(TripStatus status);
    
    boolean existsByVehicleIdAndStatus(Long vehicleId, TripStatus status);
    
    boolean existsByDriverIdAndStatus(Long driverId, TripStatus status);
    
    List<Trip> findByVehicleIdAndStatus(Long vehicleId, TripStatus status);
    
    List<Trip> findByVehicleId(Long vehicleId);

    boolean existsByDriverId(Long driverId);
}