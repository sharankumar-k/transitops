package com.transitops.backend.service;

import com.transitops.backend.dto.TripDto;
import com.transitops.backend.entity.Driver;
import com.transitops.backend.entity.FuelLog;
import com.transitops.backend.entity.Trip;
import com.transitops.backend.entity.Vehicle;
import com.transitops.backend.enums.DriverStatus;
import com.transitops.backend.enums.TripStatus;
import com.transitops.backend.enums.VehicleStatus;
import com.transitops.backend.exception.BusinessRuleViolationException;
import com.transitops.backend.exception.ResourceNotFoundException;
import com.transitops.backend.repository.DriverRepository;
import com.transitops.backend.repository.FuelLogRepository;
import com.transitops.backend.repository.TripRepository;
import com.transitops.backend.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TripService {

    private final TripRepository tripRepository;
    private final VehicleRepository vehicleRepository;
    private final DriverRepository driverRepository;
    private final FuelLogRepository fuelLogRepository;

    @Transactional
    public TripDto.Response createTrip(TripDto.CreateRequest request) {
        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));
        Driver driver = driverRepository.findById(request.getDriverId())
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found"));

        if (request.getCargoWeight().compareTo(vehicle.getMaximumLoadCapacity()) > 0) {
            throw new BusinessRuleViolationException(
                String.format("Cargo weight %s kg exceeds vehicle capacity of %s kg", 
                    request.getCargoWeight(), vehicle.getMaximumLoadCapacity()));
        }

        Trip trip = Trip.builder()
                .source(request.getSource())
                .destination(request.getDestination())
                .vehicle(vehicle)
                .driver(driver)
                .cargoWeight(request.getCargoWeight())
                .plannedDistance(request.getPlannedDistance())
                .revenue(request.getRevenue() != null ? request.getRevenue() : BigDecimal.ZERO)
                .status(TripStatus.DRAFT)
                .build();

        return mapToResponse(tripRepository.save(trip));
    }

    @Transactional
    public TripDto.Response dispatchTrip(Long id) {
        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found"));

        if (trip.getStatus() != TripStatus.DRAFT) {
            throw new BusinessRuleViolationException("Only DRAFT trips can be dispatched");
        }

        Vehicle vehicle = vehicleRepository.findByIdForUpdate(trip.getVehicle().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));
        Driver driver = driverRepository.findByIdForUpdate(trip.getDriver().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found"));

        if (vehicle.getStatus() != VehicleStatus.AVAILABLE) {
            throw new BusinessRuleViolationException("Vehicle " + vehicle.getRegistrationNumber() + " is currently " + vehicle.getStatus());
        }

        if (driver.getStatus() != DriverStatus.AVAILABLE) {
            throw new BusinessRuleViolationException("Driver " + driver.getName() + " is currently " + driver.getStatus());
        }

        if (driver.getLicenseExpiryDate().isBefore(LocalDate.now())) {
            throw new BusinessRuleViolationException("Driver license expired on " + driver.getLicenseExpiryDate());
        }

        if (tripRepository.existsByVehicleIdAndStatus(vehicle.getId(), TripStatus.DISPATCHED)) {
            throw new BusinessRuleViolationException("Vehicle " + vehicle.getRegistrationNumber() + " is already assigned to a DISPATCHED trip");
        }

        if (tripRepository.existsByDriverIdAndStatus(driver.getId(), TripStatus.DISPATCHED)) {
            throw new BusinessRuleViolationException("Driver " + driver.getName() + " is already assigned to a DISPATCHED trip");
        }

        trip.setStatus(TripStatus.DISPATCHED);
        trip.setDispatchedAt(LocalDateTime.now());
        trip.setStartOdometer(vehicle.getOdometer());

        vehicle.setStatus(VehicleStatus.ON_TRIP);
        driver.setStatus(DriverStatus.ON_TRIP);

        vehicleRepository.save(vehicle);
        driverRepository.save(driver);
        
        return mapToResponse(tripRepository.save(trip));
    }

    @Transactional
    public TripDto.Response completeTrip(Long id, TripDto.CompleteRequest request) {
        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found"));

        if (trip.getStatus() != TripStatus.DISPATCHED) {
            throw new BusinessRuleViolationException("Only DISPATCHED trips can be completed");
        }

        Vehicle vehicle = vehicleRepository.findByIdForUpdate(trip.getVehicle().getId()).orElseThrow();
        Driver driver = driverRepository.findByIdForUpdate(trip.getDriver().getId()).orElseThrow();

        if (request.getFinalOdometer().compareTo(vehicle.getOdometer()) < 0) {
            throw new BusinessRuleViolationException("Final odometer cannot be less than current vehicle odometer");
        }

        if (fuelLogRepository.existsByTripId(id)) {
            throw new BusinessRuleViolationException("Trip has already been processed with fuel metrics");
        }

        trip.setStatus(TripStatus.COMPLETED);
        trip.setCompletedAt(LocalDateTime.now());
        trip.setFinalOdometer(request.getFinalOdometer());
        
        if (request.getRevenue() != null) {
            trip.setRevenue(request.getRevenue());
        }

        vehicle.setOdometer(request.getFinalOdometer());
        vehicle.setStatus(VehicleStatus.AVAILABLE);
        driver.setStatus(DriverStatus.AVAILABLE);

        if (request.getFuelLiters() != null && request.getFuelLiters().signum() > 0) {
            FuelLog fuelLog = FuelLog.builder()
                    .vehicle(vehicle)
                    .trip(trip)
                    .liters(request.getFuelLiters())
                    .cost(request.getFuelCost() != null ? request.getFuelCost() : BigDecimal.ZERO)
                    .date(LocalDate.now())
                    .build();
            fuelLogRepository.save(fuelLog);
        }

        vehicleRepository.save(vehicle);
        driverRepository.save(driver);

        return mapToResponse(tripRepository.save(trip));
    }

    @Transactional
    public TripDto.Response cancelTrip(Long id) {
        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found"));

        if (trip.getStatus() == TripStatus.COMPLETED || trip.getStatus() == TripStatus.CANCELLED) {
            throw new BusinessRuleViolationException("Cannot cancel a " + trip.getStatus() + " trip");
        }

        if (trip.getStatus() == TripStatus.DISPATCHED) {
            Vehicle vehicle = vehicleRepository.findByIdForUpdate(trip.getVehicle().getId()).orElseThrow();
            Driver driver = driverRepository.findByIdForUpdate(trip.getDriver().getId()).orElseThrow();
            
            vehicle.setStatus(VehicleStatus.AVAILABLE);
            driver.setStatus(DriverStatus.AVAILABLE);
            
            vehicleRepository.save(vehicle);
            driverRepository.save(driver);
        }

        trip.setStatus(TripStatus.CANCELLED);
        trip.setCancelledAt(LocalDateTime.now());

        return mapToResponse(tripRepository.save(trip));
    }

    @Transactional(readOnly = true)
    public List<TripDto.Response> getAllTrips() {
        return tripRepository.findAll().stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    private TripDto.Response mapToResponse(Trip trip) {
        return TripDto.Response.builder()
                .id(trip.getId())
                .source(trip.getSource())
                .destination(trip.getDestination())
                .vehicleId(trip.getVehicle().getId())
                .vehicleRegistration(trip.getVehicle().getRegistrationNumber())
                .driverId(trip.getDriver().getId())
                .driverName(trip.getDriver().getName())
                .cargoWeight(trip.getCargoWeight())
                .plannedDistance(trip.getPlannedDistance())
                .startOdometer(trip.getStartOdometer())
                .finalOdometer(trip.getFinalOdometer())
                .revenue(trip.getRevenue())
                .status(trip.getStatus())
                .createdAt(trip.getCreatedAt())
                .dispatchedAt(trip.getDispatchedAt())
                .completedAt(trip.getCompletedAt())
                .cancelledAt(trip.getCancelledAt())
                .build();
    }
}