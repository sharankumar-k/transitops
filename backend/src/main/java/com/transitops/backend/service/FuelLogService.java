package com.transitops.backend.service;

import com.transitops.backend.dto.FuelLogDto;
import com.transitops.backend.entity.FuelLog;
import com.transitops.backend.entity.Trip;
import com.transitops.backend.entity.Vehicle;
import com.transitops.backend.exception.BusinessRuleViolationException;
import com.transitops.backend.exception.ResourceNotFoundException;
import com.transitops.backend.repository.FuelLogRepository;
import com.transitops.backend.repository.TripRepository;
import com.transitops.backend.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FuelLogService {

    private final FuelLogRepository fuelLogRepository;
    private final VehicleRepository vehicleRepository;
    private final TripRepository tripRepository;

    @Transactional
    public FuelLogDto.Response createFuelLog(FuelLogDto.Request request) {
        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));

        Trip trip = null;
        if (request.getTripId() != null) {
            if (fuelLogRepository.existsByTripId(request.getTripId())) {
                throw new BusinessRuleViolationException("A fuel log already exists for trip ID: " + request.getTripId());
            }
            
            trip = tripRepository.findById(request.getTripId())
                    .orElseThrow(() -> new ResourceNotFoundException("Trip not found"));
            if (!trip.getVehicle().getId().equals(vehicle.getId())) {
                throw new BusinessRuleViolationException("Trip does not belong to the selected vehicle");
            }
        }

        FuelLog fuelLog = FuelLog.builder()
                .vehicle(vehicle)
                .trip(trip)
                .liters(request.getLiters())
                .cost(request.getCost())
                .date(request.getDate())
                .build();

        return mapToResponse(fuelLogRepository.save(fuelLog));
    }

    @Transactional(readOnly = true)
    public List<FuelLogDto.Response> getAllFuelLogs() {
        return fuelLogRepository.findAll().stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<FuelLogDto.Response> getFuelLogsByVehicle(Long vehicleId) {
        return fuelLogRepository.findByVehicleId(vehicleId).stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<FuelLogDto.Response> getFuelLogsByTrip(Long tripId) {
        return fuelLogRepository.findByTripId(tripId).stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    private FuelLogDto.Response mapToResponse(FuelLog log) {
        return FuelLogDto.Response.builder()
                .id(log.getId())
                .vehicleId(log.getVehicle().getId())
                .vehicleRegistration(log.getVehicle().getRegistrationNumber())
                .tripId(log.getTrip() != null ? log.getTrip().getId() : null)
                .liters(log.getLiters())
                .cost(log.getCost())
                .date(log.getDate())
                .build();
    }
}