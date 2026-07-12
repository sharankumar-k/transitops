package com.transitops.backend.controller;

import com.transitops.backend.dto.DriverDto;
import com.transitops.backend.enums.DriverStatus;
import com.transitops.backend.service.DriverService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.List;

@RestController
@RequestMapping("/api/drivers")
@RequiredArgsConstructor
public class DriverController {

    private final DriverService driverService;

    @PostMapping
    @PreAuthorize("hasAnyRole('FLEET_MANAGER', 'SAFETY_OFFICER')")
    @ResponseStatus(HttpStatus.CREATED)
    public DriverDto.Response createDriver(@Valid @RequestBody DriverDto.Request request) {
        return driverService.createDriver(request);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('FLEET_MANAGER', 'DRIVER', 'SAFETY_OFFICER')")
    public List<DriverDto.Response> getAllDrivers() {
        return driverService.getAllDrivers();
    }

    @GetMapping("/available")
    @PreAuthorize("hasAnyRole('FLEET_MANAGER', 'DRIVER', 'SAFETY_OFFICER')")
    public List<DriverDto.Response> getAvailableDrivers() {
        return driverService.getAvailableDrivers();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('FLEET_MANAGER', 'DRIVER', 'SAFETY_OFFICER')")
    public DriverDto.Response getDriverById(@PathVariable Long id) {
        return driverService.getDriverById(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('FLEET_MANAGER', 'SAFETY_OFFICER')")
    public DriverDto.Response updateDriver(@PathVariable Long id, @Valid @RequestBody DriverDto.Request request) {
        return driverService.updateDriver(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('FLEET_MANAGER', 'SAFETY_OFFICER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDriver(@PathVariable Long id) {
        driverService.deleteDriver(id);
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('FLEET_MANAGER', 'DRIVER', 'SAFETY_OFFICER')")
    public List<DriverDto.Response> searchDrivers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String licenseNumber,
            @RequestParam(required = false) DriverStatus status,
            @RequestParam(required = false) String region) {
        return driverService.searchDrivers(name, licenseNumber, status, region);
    }
}