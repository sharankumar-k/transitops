package com.transitops.backend.repository;

import com.transitops.backend.entity.Driver;
import com.transitops.backend.enums.DriverStatus;
import org.springframework.data.jpa.domain.Specification;

public class DriverSpecification {

    public static Specification<Driver> filterDrivers(String name, String licenseNumber, DriverStatus status, String region) {
        return (root, query, cb) -> {
            var p = cb.conjunction();
            if (name != null && !name.trim().isEmpty()) {
                p = cb.and(p, cb.like(cb.upper(root.get("name")), "%" + name.trim().toUpperCase() + "%"));
            }
            if (licenseNumber != null && !licenseNumber.trim().isEmpty()) {
                p = cb.and(p, cb.like(cb.upper(root.get("licenseNumber")), "%" + licenseNumber.trim().toUpperCase() + "%"));
            }
            if (status != null) {
                p = cb.and(p, cb.equal(root.get("status"), status));
            }
            if (region != null && !region.trim().isEmpty()) {
                p = cb.and(p, cb.equal(cb.upper(root.get("region")), region.trim().toUpperCase()));
            }
            return p;
        };
    }
}