package com.transitops.backend.repository;

import com.transitops.backend.entity.FuelLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FuelLogRepository extends JpaRepository<FuelLog, Long> {
    List<FuelLog> findByVehicleId(Long vehicleId);
    List<FuelLog> findByTripId(Long tripId);
    boolean existsByTripId(Long tripId);
}