package com.transitops.backend.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

public class DashboardDto {

    @Data
    @Builder
    public static class Response {
        private long activeVehicles;
        private long availableVehicles;
        private long vehiclesInMaintenance;
        private long activeTrips;
        private long pendingTrips;
        private long driversOnDuty;
        private BigDecimal fleetUtilizationPercentage;
    }
}