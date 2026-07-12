package com.transitops.backend.service;

import com.transitops.backend.dto.DashboardDto;
import com.transitops.backend.enums.DriverStatus;
import com.transitops.backend.enums.TripStatus;
import com.transitops.backend.enums.VehicleStatus;
import com.transitops.backend.repository.DriverRepository;
import com.transitops.backend.repository.DriverSpecification;
import com.transitops.backend.repository.TripRepository;
import com.transitops.backend.repository.VehicleRepository;
import com.transitops.backend.repository.VehicleSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final VehicleRepository vehicleRepository;
    private final DriverRepository driverRepository;
    private final TripRepository tripRepository;

    @Transactional(readOnly = true)
    public DashboardDto.Response getDashboardStats(String vehicleType, VehicleStatus status, String region) {
        // Compose vehicle base filters from HTTP parameters
        Specification<com.transitops.backend.entity.Vehicle> baseFilter = VehicleSpecification.withType(vehicleType)
                .and(VehicleSpecification.withRegion(region));
                
        if (status != null) {
            baseFilter = baseFilter.and(VehicleSpecification.withStatus(status));
        }

        // Derive specific operational metrics cleanly at DB level via composed count queries
        long activeVehicles = vehicleRepository.count(baseFilter.and(VehicleSpecification.withStatusNot(VehicleStatus.RETIRED)));
        long availableVehicles = vehicleRepository.count(baseFilter.and(VehicleSpecification.withStatus(VehicleStatus.AVAILABLE)));
        long vehiclesInMaintenance = vehicleRepository.count(baseFilter.and(VehicleSpecification.withStatus(VehicleStatus.IN_SHOP)));
        long vehiclesOnTrip = vehicleRepository.count(baseFilter.and(VehicleSpecification.withStatus(VehicleStatus.ON_TRIP)));

        // Core workflow metric allocations
        long activeTrips = tripRepository.countByStatus(TripStatus.DISPATCHED);
        long pendingTrips = tripRepository.countByStatus(TripStatus.DRAFT);
        
        long driversOnDuty = driverRepository.count(DriverSpecification.filterDrivers(null, null, DriverStatus.ON_TRIP, region));

        BigDecimal fleetUtilization = BigDecimal.ZERO;
        if (activeVehicles > 0) {
            fleetUtilization = BigDecimal.valueOf(vehiclesOnTrip)
                    .divide(BigDecimal.valueOf(activeVehicles), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(2, RoundingMode.HALF_UP);
        }

        return DashboardDto.Response.builder()
                .activeVehicles(activeVehicles)
                .availableVehicles(availableVehicles)
                .vehiclesInMaintenance(vehiclesInMaintenance)
                .activeTrips(activeTrips)
                .pendingTrips(pendingTrips)
                .driversOnDuty(driversOnDuty)
                .fleetUtilizationPercentage(fleetUtilization)
                .build();
    }
}