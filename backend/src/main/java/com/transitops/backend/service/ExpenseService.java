package com.transitops.backend.service;

import com.transitops.backend.dto.ExpenseDto;
import com.transitops.backend.entity.Expense;
import com.transitops.backend.entity.Trip;
import com.transitops.backend.entity.Vehicle;
import com.transitops.backend.enums.ExpenseType;
import com.transitops.backend.exception.BusinessRuleViolationException;
import com.transitops.backend.exception.ResourceNotFoundException;
import com.transitops.backend.repository.ExpenseRepository;
import com.transitops.backend.repository.TripRepository;
import com.transitops.backend.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final VehicleRepository vehicleRepository;
    private final TripRepository tripRepository;

    @Transactional
    public ExpenseDto.Response createExpense(ExpenseDto.Request request) {
        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));

        if (request.getExpenseType() == ExpenseType.MAINTENANCE) {
            throw new BusinessRuleViolationException("Generic expenses cannot use core MAINTENANCE type. Use the Maintenance Workflow module instead.");
        }

        Trip trip = null;
        if (request.getTripId() != null) {
            trip = tripRepository.findById(request.getTripId())
                    .orElseThrow(() -> new ResourceNotFoundException("Trip not found"));
            if (!trip.getVehicle().getId().equals(vehicle.getId())) {
                throw new BusinessRuleViolationException("Trip does not belong to the selected vehicle");
            }
        }

        Expense expense = Expense.builder()
                .vehicle(vehicle)
                .trip(trip)
                .expenseType(request.getExpenseType())
                .description(request.getDescription())
                .amount(request.getAmount())
                .expenseDate(request.getExpenseDate())
                .build();

        return mapToResponse(expenseRepository.save(expense));
    }

    @Transactional(readOnly = true)
    public List<ExpenseDto.Response> getAllExpenses() {
        return expenseRepository.findAll().stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ExpenseDto.Response> getExpensesByVehicle(Long vehicleId) {
        return expenseRepository.findByVehicleId(vehicleId).stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ExpenseDto.Response> getExpensesByTrip(Long tripId) {
        return expenseRepository.findByTripId(tripId).stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    private ExpenseDto.Response mapToResponse(Expense expense) {
        return ExpenseDto.Response.builder()
                .id(expense.getId())
                .vehicleId(expense.getVehicle().getId())
                .vehicleRegistration(expense.getVehicle().getRegistrationNumber())
                .tripId(expense.getTrip() != null ? expense.getTrip().getId() : null)
                .expenseType(expense.getExpenseType())
                .description(expense.getDescription())
                .amount(expense.getAmount())
                .expenseDate(expense.getExpenseDate())
                .build();
    }
}