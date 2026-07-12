package com.transitops.backend.controller;

import com.transitops.backend.dto.VehicleDto;
import com.transitops.backend.enums.VehicleStatus;
import com.transitops.backend.service.VehicleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('FLEET_MANAGER')")
    public VehicleDto.Response createVehicle(@Valid @RequestBody VehicleDto.Request request) {
        return vehicleService.createVehicle(request);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<VehicleDto.Response> getAllVehicles() {
        return vehicleService.getAllVehicles();
    }

    @GetMapping("/available")
    @PreAuthorize("isAuthenticated()")
    public List<VehicleDto.Response> getAvailableVehicles() {
        return vehicleService.getAvailableVehicles();
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public VehicleDto.Response getVehicleById(@PathVariable Long id) {
        return vehicleService.getVehicleById(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('FLEET_MANAGER')")
    public VehicleDto.Response updateVehicle(@PathVariable Long id, @Valid @RequestBody VehicleDto.Request request) {
        return vehicleService.updateVehicle(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('FLEET_MANAGER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteVehicle(@PathVariable Long id) {
        vehicleService.deleteVehicle(id);
    }

    @GetMapping("/search")
    @PreAuthorize("isAuthenticated()")
    public List<VehicleDto.Response> searchVehicles(
            @RequestParam(required = false) String registrationNumber,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) VehicleStatus status,
            @RequestParam(required = false) String region) {
        return vehicleService.searchVehicles(registrationNumber, type, status, region);
    }
}