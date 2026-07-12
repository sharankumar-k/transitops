package com.transitops.backend.repository;

import com.transitops.backend.entity.Driver;
import com.transitops.backend.enums.DriverStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DriverRepository extends JpaRepository<Driver, Long>, JpaSpecificationExecutor<Driver> {
    
    boolean existsByLicenseNumber(String licenseNumber);

    @Query("SELECT d FROM Driver d WHERE d.status = 'AVAILABLE' AND d.licenseExpiryDate >= :currentDate")
    List<Driver> findAvailableDrivers(@Param("currentDate") LocalDate currentDate);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT d FROM Driver d WHERE d.id = :id")
    Optional<Driver> findByIdForUpdate(@Param("id") Long id);

    long countByStatus(DriverStatus status);
}