package com.transitops.backend.controller;

import com.transitops.backend.dto.MaintenanceDto;
import com.transitops.backend.service.MaintenanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/maintenance")
@RequiredArgsConstructor
public class MaintenanceController {

    private final MaintenanceService maintenanceService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MaintenanceDto.Response createMaintenance(@Valid @RequestBody MaintenanceDto.CreateRequest request) {
        return maintenanceService.createMaintenance(request);
    }

    @PostMapping("/{id}/close")
    public MaintenanceDto.Response closeMaintenance(@PathVariable Long id, @Valid @RequestBody MaintenanceDto.CloseRequest request) {
        return maintenanceService.closeMaintenance(id, request);
    }

    @GetMapping
    public List<MaintenanceDto.Response> getAllMaintenance() {
        return maintenanceService.getAllMaintenance();
    }
}