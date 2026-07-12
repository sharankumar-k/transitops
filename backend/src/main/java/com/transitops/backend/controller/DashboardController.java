package com.transitops.backend.controller;

import com.transitops.backend.dto.DashboardDto;
import com.transitops.backend.enums.VehicleStatus;
import com.transitops.backend.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public DashboardDto.Response getDashboardStats(
            @RequestParam(required = false) String vehicleType,
            @RequestParam(required = false) VehicleStatus vehicleStatus,
            @RequestParam(required = false) String region) {
        return dashboardService.getDashboardStats(vehicleType, vehicleStatus, region);
    }
}