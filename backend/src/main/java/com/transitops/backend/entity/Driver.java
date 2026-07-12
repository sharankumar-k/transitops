package com.transitops.backend.entity;

import com.transitops.backend.enums.DriverStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "drivers", indexes = {
    @Index(name = "idx_driver_status", columnList = "status"),
    @Index(name = "idx_driver_region", columnList = "region")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true, length = 50)
    private String licenseNumber;

    private String licenseCategory;

    @Column(nullable = false)
    private LocalDate licenseExpiryDate;

    private String contactNumber;

    @Column(nullable = false)
    private Integer safetyScore;

    private String region;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DriverStatus status;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}