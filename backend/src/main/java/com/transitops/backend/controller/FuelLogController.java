package com.transitops.backend.controller;

import com.transitops.backend.dto.FuelLogDto;
import com.transitops.backend.service.FuelLogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.List;

@RestController
@RequestMapping("/api/fuel-logs")
@RequiredArgsConstructor
public class FuelLogController {

    private final FuelLogService fuelLogService;

    @PostMapping
    @PreAuthorize("hasAnyRole('FLEET_MANAGER', 'DRIVER', 'FINANCIAL_ANALYST')")
    @ResponseStatus(HttpStatus.CREATED)
    public FuelLogDto.Response createFuelLog(@Valid @RequestBody FuelLogDto.Request request) {
        return fuelLogService.createFuelLog(request);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('FLEET_MANAGER', 'FINANCIAL_ANALYST')")
    public List<FuelLogDto.Response> getAllFuelLogs() {
        return fuelLogService.getAllFuelLogs();
    }

    @GetMapping("/vehicle/{vehicleId}")
    @PreAuthorize("hasAnyRole('FLEET_MANAGER', 'FINANCIAL_ANALYST')")
    public List<FuelLogDto.Response> getFuelLogsByVehicle(@PathVariable Long vehicleId) {
        return fuelLogService.getFuelLogsByVehicle(vehicleId);
    }

    @GetMapping("/trip/{tripId}")
    @PreAuthorize("hasAnyRole('FLEET_MANAGER', 'FINANCIAL_ANALYST')")
    public List<FuelLogDto.Response> getFuelLogsByTrip(@PathVariable Long tripId) {
        return fuelLogService.getFuelLogsByTrip(tripId);
    }
}