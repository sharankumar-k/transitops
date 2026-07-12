package com.transitops.backend.service;

import com.transitops.backend.dto.VehicleDto;
import com.transitops.backend.entity.Vehicle;
import com.transitops.backend.enums.VehicleStatus;
import com.transitops.backend.exception.BusinessRuleViolationException;
import com.transitops.backend.exception.ResourceNotFoundException;
import com.transitops.backend.repository.VehicleRepository;
import com.transitops.backend.repository.VehicleSpecification;
import com.transitops.backend.repository.TripRepository;
import com.transitops.backend.repository.MaintenanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final TripRepository tripRepository;
    private final MaintenanceRepository maintenanceRepository;

    @Transactional
    public VehicleDto.Response createVehicle(VehicleDto.Request request) {
        String regNumber = request.getRegistrationNumber().trim().toUpperCase();
        if (vehicleRepository.existsByRegistrationNumber(regNumber)) {
            throw new BusinessRuleViolationException("Vehicle with registration number " + regNumber + " already exists.");
        }

        Vehicle vehicle = Vehicle.builder()
                .registrationNumber(regNumber)
                .name(request.getName())
                .model(request.getModel())
                .type(request.getType())
                .maximumLoadCapacity(request.getMaximumLoadCapacity())
                .odometer(request.getOdometer())
                .acquisitionCost(request.getAcquisitionCost())
                .region(request.getRegion())
                .status(VehicleStatus.AVAILABLE)
                .build();

        return mapToResponse(vehicleRepository.save(vehicle));
    }

    @Transactional(readOnly = true)
    public List<VehicleDto.Response> getAllVehicles() {
        return vehicleRepository.findAll().stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<VehicleDto.Response> getAvailableVehicles() {
        return vehicleRepository.findByStatus(VehicleStatus.AVAILABLE).stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public VehicleDto.Response getVehicleById(Long id) {
        return vehicleRepository.findById(id).map(this::mapToResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with ID: " + id));
    }

    @Transactional
    public VehicleDto.Response updateVehicle(Long id, VehicleDto.Request request) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with ID: " + id));

        String regNumber = request.getRegistrationNumber().trim().toUpperCase();
        if (!vehicle.getRegistrationNumber().equals(regNumber) && vehicleRepository.existsByRegistrationNumber(regNumber)) {
            throw new BusinessRuleViolationException("Registration number " + regNumber + " is already taken.");
        }

        vehicle.setRegistrationNumber(regNumber);
        vehicle.setName(request.getName());
        vehicle.setModel(request.getModel());
        vehicle.setType(request.getType());
        vehicle.setMaximumLoadCapacity(request.getMaximumLoadCapacity());
        vehicle.setOdometer(request.getOdometer());
        vehicle.setAcquisitionCost(request.getAcquisitionCost());
        vehicle.setRegion(request.getRegion());

        return mapToResponse(vehicleRepository.save(vehicle));
    }

    @Transactional
    public void deleteVehicle(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with ID: " + id));

        if (vehicle.getStatus() == VehicleStatus.ON_TRIP) {
            throw new BusinessRuleViolationException("Cannot delete vehicle while it is ON_TRIP.");
        }
        if (vehicle.getStatus() == VehicleStatus.IN_SHOP) {
            throw new BusinessRuleViolationException("Cannot delete vehicle while it is IN_SHOP.");
        }

        boolean hasTrips = !tripRepository.findByVehicleId(id).isEmpty();
        boolean hasMaintenance = maintenanceRepository.existsByVehicleId(id);

        if (hasTrips || hasMaintenance) {
            vehicle.setStatus(VehicleStatus.RETIRED);
            vehicleRepository.save(vehicle);
        } else {
            vehicleRepository.delete(vehicle);
        }
    }

    @Transactional(readOnly = true)
    public List<VehicleDto.Response> searchVehicles(String registrationNumber, String type, VehicleStatus status, String region) {
        return vehicleRepository.findAll(VehicleSpecification.filterVehicles(registrationNumber, type, status, region))
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public VehicleDto.Response mapToResponse(Vehicle vehicle) {
        return VehicleDto.Response.builder()
                .id(vehicle.getId())
                .registrationNumber(vehicle.getRegistrationNumber())
                .name(vehicle.getName())
                .model(vehicle.getModel())
                .type(vehicle.getType())
                .maximumLoadCapacity(vehicle.getMaximumLoadCapacity())
                .odometer(vehicle.getOdometer())
                .acquisitionCost(vehicle.getAcquisitionCost())
                .region(vehicle.getRegion())
                .status(vehicle.getStatus())
                .build();
    }
}