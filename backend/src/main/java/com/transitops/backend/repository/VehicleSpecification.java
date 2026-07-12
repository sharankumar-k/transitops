package com.transitops.backend.repository;

import com.transitops.backend.entity.Vehicle;
import com.transitops.backend.enums.VehicleStatus;
import org.springframework.data.jpa.domain.Specification;

public class VehicleSpecification {

    public static Specification<Vehicle> withRegistrationNumber(String registrationNumber) {
        return (root, query, cb) -> {
            if (registrationNumber == null || registrationNumber.trim().isEmpty()) return null;
            return cb.like(cb.upper(root.get("registrationNumber")), "%" + registrationNumber.trim().toUpperCase() + "%");
        };
    }

    public static Specification<Vehicle> withType(String type) {
        return (root, query, cb) -> {
            if (type == null || type.trim().isEmpty()) return null;
            return cb.equal(cb.upper(root.get("type")), type.trim().toUpperCase());
        };
    }

    public static Specification<Vehicle> withStatus(VehicleStatus status) {
        return (root, query, cb) -> {
            if (status == null) return null;
            return cb.equal(root.get("status"), status);
        };
    }

    public static Specification<Vehicle> withStatusNot(VehicleStatus status) {
        return (root, query, cb) -> {
            if (status == null) return null;
            return cb.notEqual(root.get("status"), status);
        };
    }

    public static Specification<Vehicle> withRegion(String region) {
        return (root, query, cb) -> {
            if (region == null || region.trim().isEmpty()) return null;
            return cb.equal(cb.upper(root.get("region")), region.trim().toUpperCase());
        };
    }

    public static Specification<Vehicle> filterVehicles(String registrationNumber, String type, VehicleStatus status, String region) {
        return Specification.where(withRegistrationNumber(registrationNumber))
                .and(withType(type))
                .and(withStatus(status))
                .and(withRegion(region));
    }
}