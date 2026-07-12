package com.transitops.backend.controller;

import com.transitops.backend.dto.TripDto;
import com.transitops.backend.service.TripService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/api/trips")
@RequiredArgsConstructor
public class TripController {

    private final TripService tripService;

    @PostMapping
    @PreAuthorize("hasAnyRole('FLEET_MANAGER', 'DRIVER')")
    @ResponseStatus(HttpStatus.CREATED)
    public TripDto.Response createTrip(@Valid @RequestBody TripDto.CreateRequest request) {
        return tripService.createTrip(request);
    }
    
    @PostMapping("/{id}/dispatch")
    @PreAuthorize("hasAnyRole('FLEET_MANAGER', 'DRIVER')")
    public TripDto.Response dispatchTrip(@PathVariable Long id) {
        return tripService.dispatchTrip(id);
    }

    @PostMapping("/{id}/complete")
    @PreAuthorize("hasAnyRole('FLEET_MANAGER', 'DRIVER')")
    public TripDto.Response completeTrip(@PathVariable Long id, @Valid @RequestBody TripDto.CompleteRequest request) {
        return tripService.completeTrip(id, request);
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('FLEET_MANAGER', 'DRIVER')")
    public TripDto.Response cancelTrip(@PathVariable Long id) {
        return tripService.cancelTrip(id);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('FLEET_MANAGER', 'DRIVER', 'FINANCIAL_ANALYST')")
    public List<TripDto.Response> getAllTrips() {
        return tripService.getAllTrips();
    }
}