package com.transitops.backend.entity;

import com.transitops.backend.enums.VehicleStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "vehicles", indexes = {
    @Index(name = "idx_vehicle_status", columnList = "status"),
    @Index(name = "idx_vehicle_region", columnList = "region")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String registrationNumber;

    @Column(nullable = false)
    private String name;

    private String model;
    private String type;

    @Column(nullable = false)
    private BigDecimal maximumLoadCapacity;

    @Column(nullable = false)
    private BigDecimal odometer;

    @Column(nullable = false)
    private BigDecimal acquisitionCost;

    private String region;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private VehicleStatus status;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}