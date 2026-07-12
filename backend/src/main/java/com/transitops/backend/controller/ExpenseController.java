package com.transitops.backend.controller;

import com.transitops.backend.dto.ExpenseDto;
import com.transitops.backend.service.ExpenseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.List;

@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('FLEET_MANAGER', 'FINANCIAL_ANALYST')")
    public ExpenseDto.Response createExpense(@Valid @RequestBody ExpenseDto.Request request) {
        return expenseService.createExpense(request);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('FLEET_MANAGER', 'FINANCIAL_ANALYST')")
    public List<ExpenseDto.Response> getAllExpenses() {
        return expenseService.getAllExpenses();
    }

    @GetMapping("/vehicle/{vehicleId}")
    @PreAuthorize("hasAnyRole('FLEET_MANAGER', 'FINANCIAL_ANALYST')")
    public List<ExpenseDto.Response> getExpensesByVehicle(@PathVariable Long vehicleId) {
        return expenseService.getExpensesByVehicle(vehicleId);
    }

    @GetMapping("/trip/{tripId}")
    @PreAuthorize("hasAnyRole('FLEET_MANAGER', 'FINANCIAL_ANALYST')")
    public List<ExpenseDto.Response> getExpensesByTrip(@PathVariable Long tripId) {
        return expenseService.getExpensesByTrip(tripId);
    }
}