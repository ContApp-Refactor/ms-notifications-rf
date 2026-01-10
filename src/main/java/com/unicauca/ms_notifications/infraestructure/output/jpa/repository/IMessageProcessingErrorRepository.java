package com.unicauca.ms_notifications.infraestructure.output.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.unicauca.ms_notifications.infraestructure.output.jpa.entity.MessageProcessingErrorEntity;

@Repository
public interface IMessageProcessingErrorRepository extends JpaRepository<MessageProcessingErrorEntity, Long> {
}
