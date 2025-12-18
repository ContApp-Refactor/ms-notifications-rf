package com.unicauca.ms_notifications.infraestructure.output.jpa.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.unicauca.ms_notifications.infraestructure.output.jpa.entity.ThirdReplicaEntity;

@Repository
public interface IThirdReplicaRepository extends JpaRepository<ThirdReplicaEntity, Long> {
    Optional<ThirdReplicaEntity> findByThirdId(Long thirdId);
}
