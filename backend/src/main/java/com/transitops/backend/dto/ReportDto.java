package com.transitops.backend.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

public class ReportDto {

    @Data
    @Builder
    public static class VehicleAnalytics {
        private Long vehicleId;
        private String registrationNumber;
        private String vehicleName;
        private BigDecimal totalCompletedDistance;
        private BigDecimal totalFuelLiters;
        private BigDecimal fuelEfficiency;
        private BigDecimal totalFuelCost;
        private BigDecimal totalMaintenanceCost;
        private BigDecimal totalOtherExpenses;
        private BigDecimal totalOperationalCost;
        private BigDecimal totalRevenue;
        private BigDecimal vehicleROI;
    }
}