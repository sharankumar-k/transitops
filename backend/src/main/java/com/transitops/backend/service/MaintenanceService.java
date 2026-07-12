package com.transitops.backend.service;

import com.transitops.backend.dto.MaintenanceDto;
import com.transitops.backend.entity.Maintenance;
import com.transitops.backend.entity.Vehicle;
import com.transitops.backend.enums.MaintenanceStatus;
import com.transitops.backend.enums.VehicleStatus;
import com.transitops.backend.exception.BusinessRuleViolationException;
import com.transitops.backend.exception.ResourceNotFoundException;
import com.transitops.backend.repository.MaintenanceRepository;
import com.transitops.backend.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MaintenanceService {

    private final MaintenanceRepository maintenanceRepository;
    private final VehicleRepository vehicleRepository;

    @Transactional
    public MaintenanceDto.Response createMaintenance(MaintenanceDto.CreateRequest request) {
        Vehicle vehicle = vehicleRepository.findByIdForUpdate(request.getVehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));

        if (vehicle.getStatus() == VehicleStatus.RETIRED) {
            throw new BusinessRuleViolationException("Cannot perform maintenance on a RETIRED vehicle");
        }
        if (vehicle.getStatus() == VehicleStatus.ON_TRIP) {
            throw new BusinessRuleViolationException("Cannot perform maintenance on an ON_TRIP vehicle");
        }
        if (maintenanceRepository.existsByVehicleIdAndStatus(vehicle.getId(), MaintenanceStatus.ACTIVE)) {
            throw new BusinessRuleViolationException("Vehicle is already in ACTIVE maintenance");
        }

        Maintenance maintenance = Maintenance.builder()
                .vehicle(vehicle)
                .description(request.getDescription())
                .startDate(request.getStartDate())
                .status(MaintenanceStatus.ACTIVE)
                .build();

        vehicle.setStatus(VehicleStatus.IN_SHOP);
        vehicleRepository.save(vehicle);

        return mapToResponse(maintenanceRepository.save(maintenance));
    }

    @Transactional
    public MaintenanceDto.Response closeMaintenance(Long id, MaintenanceDto.CloseRequest request) {
        Maintenance maintenance = maintenanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Maintenance record not found"));

        if (maintenance.getStatus() != MaintenanceStatus.ACTIVE) {
            throw new BusinessRuleViolationException("Only ACTIVE maintenance records can be closed");
        }

        Vehicle vehicle = vehicleRepository.findByIdForUpdate(maintenance.getVehicle().getId()).orElseThrow();

        maintenance.setStatus(MaintenanceStatus.CLOSED);
        maintenance.setEndDate(request.getEndDate());
        maintenance.setMaintenanceCost(request.getMaintenanceCost());

        if (vehicle.getStatus() == VehicleStatus.IN_SHOP) {
            vehicle.setStatus(VehicleStatus.AVAILABLE);
            vehicleRepository.save(vehicle);
        }

        return mapToResponse(maintenanceRepository.save(maintenance));
    }

    @Transactional(readOnly = true)
    public List<MaintenanceDto.Response> getAllMaintenance() {
        return maintenanceRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private MaintenanceDto.Response mapToResponse(Maintenance maintenance) {
        return MaintenanceDto.Response.builder()
                .id(maintenance.getId())
                .vehicleId(maintenance.getVehicle().getId())
                .vehicleRegistration(maintenance.getVehicle().getRegistrationNumber())
                .description(maintenance.getDescription())
                .maintenanceCost(maintenance.getMaintenanceCost())
                .startDate(maintenance.getStartDate())
                .endDate(maintenance.getEndDate())
                .status(maintenance.getStatus())
                .build();
    }
}