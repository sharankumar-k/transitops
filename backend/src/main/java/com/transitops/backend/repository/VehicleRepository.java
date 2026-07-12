package com.transitops.backend.repository;

import com.transitops.backend.entity.Vehicle;
import com.transitops.backend.enums.VehicleStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, Long>, JpaSpecificationExecutor<Vehicle> {
    
    boolean existsByRegistrationNumber(String registrationNumber);
    
    List<Vehicle> findByStatus(VehicleStatus status);
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT v FROM Vehicle v WHERE v.id = :id")
    Optional<Vehicle> findByIdForUpdate(@Param("id") Long id);

    long countByStatusNot(VehicleStatus status);
    long countByStatus(VehicleStatus status);
}