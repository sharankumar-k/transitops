package com.transitops.backend.dto;

import com.transitops.backend.enums.TripStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TripDto {

    @Data
    public static class CreateRequest {
        @NotBlank
        private String source;
        @NotBlank
        private String destination;
        @NotNull
        private Long vehicleId;
        @NotNull
        private Long driverId;
        @NotNull
        @Positive(message = "Cargo weight must be greater than 0")
        private BigDecimal cargoWeight;
        @NotNull
        @Positive(message = "Planned distance must be greater than 0")
        private BigDecimal plannedDistance;
        @PositiveOrZero
        private BigDecimal revenue;
    }

    @Data
    public static class CompleteRequest {
        @NotNull
        @PositiveOrZero
        private BigDecimal finalOdometer;
        
        @Positive
        private BigDecimal fuelLiters;
        
        @PositiveOrZero
        private BigDecimal fuelCost;

        @PositiveOrZero
        private BigDecimal revenue;
    }

    @Data
    @Builder
    public static class Response {
        private Long id;
        private String source;
        private String destination;
        private Long vehicleId;
        private String vehicleRegistration;
        private Long driverId;
        private String driverName;
        private BigDecimal cargoWeight;
        private BigDecimal plannedDistance;
        private BigDecimal startOdometer;
        private BigDecimal finalOdometer;
        private BigDecimal revenue;
        private TripStatus status;
        private LocalDateTime createdAt;
        private LocalDateTime dispatchedAt;
        private LocalDateTime completedAt;
        private LocalDateTime cancelledAt;
    }
}