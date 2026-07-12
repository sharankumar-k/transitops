package com.transitops.backend.service;

import com.transitops.backend.dto.ReportDto;
import com.transitops.backend.entity.FuelLog;
import com.transitops.backend.entity.Maintenance;
import com.transitops.backend.entity.Expense;
import com.transitops.backend.entity.Trip;
import com.transitops.backend.entity.Vehicle;
import com.transitops.backend.enums.TripStatus;
import com.transitops.backend.exception.ResourceNotFoundException;
import com.transitops.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final VehicleRepository vehicleRepository;
    private final TripRepository tripRepository;
    private final FuelLogRepository fuelLogRepository;
    private final MaintenanceRepository maintenanceRepository;
    private final ExpenseRepository expenseRepository;

    @Transactional(readOnly = true)
    public ReportDto.VehicleAnalytics getVehicleAnalytics(Long vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));
        return calculateAnalyticsForVehicle(vehicle);
    }

    @Transactional(readOnly = true)
    public List<ReportDto.VehicleAnalytics> getAllVehicleAnalytics() {
        return vehicleRepository.findAll().stream()
                .map(this::calculateAnalyticsForVehicle)
                .collect(Collectors.toList());
    }

    private ReportDto.VehicleAnalytics calculateAnalyticsForVehicle(Vehicle vehicle) {
        List<Trip> trips = tripRepository.findByVehicleId(vehicle.getId());
        List<FuelLog> fuelLogs = fuelLogRepository.findByVehicleId(vehicle.getId());
        List<Maintenance> maintenances = maintenanceRepository.findByVehicleId(vehicle.getId());
        List<Expense> expenses = expenseRepository.findByVehicleId(vehicle.getId());

        BigDecimal totalDistance = trips.stream()
                .filter(t -> t.getStatus() == TripStatus.COMPLETED)
                .map(t -> (t.getFinalOdometer() != null && t.getStartOdometer() != null) 
                        ? t.getFinalOdometer().subtract(t.getStartOdometer()) 
                        : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalLiters = fuelLogs.stream()
                .map(FuelLog::getLiters)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal fuelEfficiency = BigDecimal.ZERO;
        if (totalLiters.signum() > 0) {
            fuelEfficiency = totalDistance.divide(totalLiters, 2, RoundingMode.HALF_UP);
        }

        BigDecimal totalFuelCost = fuelLogs.stream().map(FuelLog::getCost).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalMaintenanceCost = maintenances.stream()
                .map(m -> m.getMaintenanceCost() != null ? m.getMaintenanceCost() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalOtherExpenses = expenses.stream().map(Expense::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalOperationalCost = totalFuelCost.add(totalMaintenanceCost);
        BigDecimal totalRevenue = trips.stream()
                .filter(t -> t.getStatus() == TripStatus.COMPLETED)
                .map(t -> t.getRevenue() != null ? t.getRevenue() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal coreOperationalCost = totalMaintenanceCost.add(totalFuelCost);
        BigDecimal vehicleROI = null;
        if (vehicle.getAcquisitionCost() != null && vehicle.getAcquisitionCost().signum() > 0) {
            vehicleROI = totalRevenue.subtract(coreOperationalCost)
                    .divide(vehicle.getAcquisitionCost(), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(2, RoundingMode.HALF_UP);
        }

        return ReportDto.VehicleAnalytics.builder()
                .vehicleId(vehicle.getId())
                .registrationNumber(vehicle.getRegistrationNumber())
                .vehicleName(vehicle.getName())
                .totalCompletedDistance(totalDistance)
                .totalFuelLiters(totalLiters)
                .fuelEfficiency(fuelEfficiency)
                .totalFuelCost(totalFuelCost)
                .totalMaintenanceCost(totalMaintenanceCost)
                .totalOtherExpenses(totalOtherExpenses)
                .totalOperationalCost(totalOperationalCost)
                .totalRevenue(totalRevenue)
                .vehicleROI(vehicleROI)
                .build();
    }
}