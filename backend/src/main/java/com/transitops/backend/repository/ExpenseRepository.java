package com.transitops.backend.repository;

import com.transitops.backend.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByVehicleId(Long vehicleId);
    List<Expense> findByTripId(Long tripId);
}