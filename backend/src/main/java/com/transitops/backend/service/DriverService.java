package com.transitops.backend.service;

import com.transitops.backend.dto.DriverDto;
import com.transitops.backend.entity.Driver;
import com.transitops.backend.enums.DriverStatus;
import com.transitops.backend.exception.BusinessRuleViolationException;
import com.transitops.backend.exception.ResourceNotFoundException;
import com.transitops.backend.repository.DriverRepository;
import com.transitops.backend.repository.DriverSpecification;
import com.transitops.backend.repository.TripRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DriverService {

    private final DriverRepository driverRepository;
    private final TripRepository tripRepository;

    @Transactional
    public DriverDto.Response createDriver(DriverDto.Request request) {
        String licenseNumber = request.getLicenseNumber().trim().toUpperCase();
        if (driverRepository.existsByLicenseNumber(licenseNumber)) {
            throw new BusinessRuleViolationException("Driver with license number " + licenseNumber + " already exists.");
        }

        Driver driver = Driver.builder()
                .name(request.getName())
                .licenseNumber(licenseNumber)
                .licenseCategory(request.getLicenseCategory())
                .licenseExpiryDate(request.getLicenseExpiryDate())
                .contactNumber(request.getContactNumber())
                .safetyScore(request.getSafetyScore())
                .region(request.getRegion())
                .status(DriverStatus.AVAILABLE)
                .build();

        return mapToResponse(driverRepository.save(driver));
    }

    @Transactional(readOnly = true)
    public List<DriverDto.Response> getAllDrivers() {
        return driverRepository.findAll().stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DriverDto.Response> getAvailableDrivers() {
        return driverRepository.findAvailableDrivers(LocalDate.now()).stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DriverDto.Response getDriverById(Long id) {
        return driverRepository.findById(id).map(this::mapToResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with ID: " + id));
    }

    @Transactional
    public DriverDto.Response updateDriver(Long id, DriverDto.Request request) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with ID: " + id));

        String licenseNumber = request.getLicenseNumber().trim().toUpperCase();
        if (!driver.getLicenseNumber().equals(licenseNumber) && driverRepository.existsByLicenseNumber(licenseNumber)) {
            throw new BusinessRuleViolationException("License number " + licenseNumber + " is already assigned to another driver.");
        }

        driver.setName(request.getName());
        driver.setLicenseNumber(licenseNumber);
        driver.setLicenseCategory(request.getLicenseCategory());
        driver.setLicenseExpiryDate(request.getLicenseExpiryDate());
        driver.setContactNumber(request.getContactNumber());
        driver.setSafetyScore(request.getSafetyScore());
        driver.setRegion(request.getRegion());

        return mapToResponse(driverRepository.save(driver));
    }

    @Transactional
    public void deleteDriver(Long id) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with ID: " + id));

        if (driver.getStatus() == DriverStatus.ON_TRIP) {
            throw new BusinessRuleViolationException("Cannot delete a driver while they are ON_TRIP.");
        }
        
        if (tripRepository.existsByDriverId(id)) {
            driver.setStatus(DriverStatus.SUSPENDED);
            driverRepository.save(driver);
        } else {
            driverRepository.delete(driver);
        }
    }

    @Transactional(readOnly = true)
    public List<DriverDto.Response> searchDrivers(String name, String licenseNumber, DriverStatus status, String region) {
        return driverRepository.findAll(DriverSpecification.filterDrivers(name, licenseNumber, status, region))
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public DriverDto.Response mapToResponse(Driver driver) {
        return DriverDto.Response.builder()
                .id(driver.getId())
                .name(driver.getName())
                .licenseNumber(driver.getLicenseNumber())
                .licenseCategory(driver.getLicenseCategory())
                .licenseExpiryDate(driver.getLicenseExpiryDate())
                .contactNumber(driver.getContactNumber())
                .safetyScore(driver.getSafetyScore())
                .region(driver.getRegion())
                .status(driver.getStatus())
                .build();
    }
}