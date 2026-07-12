package com.transitops.backend.repository;

import com.transitops.backend.entity.Maintenance;
import com.transitops.backend.enums.MaintenanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MaintenanceRepository extends JpaRepository<Maintenance, Long> {
    boolean existsByVehicleIdAndStatus(Long vehicleId, MaintenanceStatus status);
    List<Maintenance> findByVehicleId(Long vehicleId);
    boolean existsByVehicleId(Long vehicleId);
}