package com.transitops.backend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

public class FuelLogDto {

    @Data
    public static class Request {
        @NotNull(message = "Vehicle ID is required")
        private Long vehicleId;

        private Long tripId;

        @NotNull(message = "Liters value is required")
        @Positive(message = "Liters must be greater than zero")
        private BigDecimal liters;

        @NotNull(message = "Cost is required")
        @PositiveOrZero(message = "Cost cannot be negative")
        private BigDecimal cost;

        @NotNull(message = "Date is required")
        private LocalDate date;
    }

    @Data
    @Builder
    public static class Response {
        private Long id;
        private Long vehicleId;
        private String vehicleRegistration;
        private Long tripId;
        private BigDecimal liters;
        private BigDecimal cost;
        private LocalDate date;
    }
}