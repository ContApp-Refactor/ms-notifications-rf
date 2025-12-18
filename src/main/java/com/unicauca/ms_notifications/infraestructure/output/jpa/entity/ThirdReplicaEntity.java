package com.unicauca.ms_notifications.infraestructure.output.jpa.entity;

import org.hibernate.annotations.TenantId;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "third_replicas")
@Data
public class ThirdReplicaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "third_id", nullable = false, unique = true)
    private Long thirdId; // El ID original del tercero

    @Column(name = "enterprise_id", nullable = false)
    private String enterpriseId;

    @Column(name = "full_name", length = 300)
    private String fullName;

    @Column(name = "email", length = 150)
    private String email;

    @Column(name = "is_active")
    private Boolean active;

    @TenantId
    String tenantId;
}
