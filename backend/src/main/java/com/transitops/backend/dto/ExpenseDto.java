package com.transitops.backend.dto;

import com.transitops.backend.enums.ExpenseType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ExpenseDto {

    @Data
    public static class Request {
        @NotNull(message = "Vehicle ID is required")
        private Long vehicleId;
        
        private Long tripId;
        
        @NotNull(message = "Expense type is required")
        private ExpenseType expenseType;
        
        @NotBlank(message = "Description is required")
        private String description;
        
        @NotNull(message = "Amount is required")
        @PositiveOrZero(message = "Amount cannot be negative")
        private BigDecimal amount;
        
        @NotNull(message = "Expense date is required")
        private LocalDate expenseDate;
    }

    @Data
    @Builder
    public static class Response {
        private Long id;
        private Long vehicleId;
        private String vehicleRegistration;
        private Long tripId;
        private ExpenseType expenseType;
        private String description;
        private BigDecimal amount;
        private LocalDate expenseDate;
    }
}