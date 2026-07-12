package com.transitops.backend.dto;

import com.transitops.backend.enums.VehicleStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

public class VehicleDto {

    @Data
    public static class Request {
        @NotBlank(message = "Registration number is required")
        private String registrationNumber;
        @NotBlank(message = "Name is required")
        private String name;
        private String model;
        private String type;
        @NotNull
        @Positive(message = "Capacity must be greater than zero")
        private BigDecimal maximumLoadCapacity;
        @NotNull
        @PositiveOrZero(message = "Odometer cannot be negative")
        private BigDecimal odometer;
        @NotNull
        @PositiveOrZero(message = "Acquisition cost cannot be negative")
        private BigDecimal acquisitionCost;
        private String region;
    }

    @Data
    @Builder
    public static class Response {
        private Long id;
        private String registrationNumber;
        private String name;
        private String model;
        private String type;
        private BigDecimal maximumLoadCapacity;
        private BigDecimal odometer;
        private BigDecimal acquisitionCost;
        private String region;
        private VehicleStatus status;
    }
}