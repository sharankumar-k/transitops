package com.transitops.backend.dto;

import com.transitops.backend.enums.DriverStatus;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

public class DriverDto {

    @Data
    public static class Request {
        @NotBlank(message = "Name is required")
        private String name;
        @NotBlank(message = "License number is required")
        private String licenseNumber;
        private String licenseCategory;
        @NotNull(message = "License expiry date is required")
        private LocalDate licenseExpiryDate;
        private String contactNumber;
        @NotNull
        @Min(value = 0, message = "Safety score must be between 0 and 100")
        @Max(value = 100, message = "Safety score must be between 0 and 100")
        private Integer safetyScore;
        private String region;
    }

    @Data
    @Builder
    public static class Response {
        private Long id;
        private String name;
        private String licenseNumber;
        private String licenseCategory;
        private LocalDate licenseExpiryDate;
        private String contactNumber;
        private Integer safetyScore;
        private String region;
        private DriverStatus status;
    }
}