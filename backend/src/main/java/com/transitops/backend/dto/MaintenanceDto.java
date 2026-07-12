package com.transitops.backend.dto;

import com.transitops.backend.enums.MaintenanceStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

public class MaintenanceDto {

    @Data
    public static class CreateRequest {
        @NotNull
        private Long vehicleId;
        @NotBlank
        private String description;
        @NotNull
        private LocalDate startDate;
    }

    @Data
    public static class CloseRequest {
        @NotNull
        private LocalDate endDate;
        @NotNull
        @PositiveOrZero
        private BigDecimal maintenanceCost;
    }

    @Data
    @Builder
    public static class Response {
        private Long id;
        private Long vehicleId;
        private String vehicleRegistration;
        private String description;
        private BigDecimal maintenanceCost;
        private LocalDate startDate;
        private LocalDate endDate;
        private MaintenanceStatus status;
    }
}