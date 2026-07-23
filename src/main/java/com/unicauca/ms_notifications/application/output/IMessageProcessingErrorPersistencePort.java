package com.unicauca.ms_notifications.application.output;

import java.util.Optional;
import com.unicauca.ms_notifications.domain.model.MessageProcessingError;

public interface IMessageProcessingErrorPersistencePort {
    Optional<MessageProcessingError> findById(Long id);
    Optional<MessageProcessingError> findLastRecord();
    void deleteAll();
}

