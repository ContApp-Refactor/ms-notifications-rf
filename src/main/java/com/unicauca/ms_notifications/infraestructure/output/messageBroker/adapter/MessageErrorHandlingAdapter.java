package com.unicauca.ms_notifications.infraestructure.output.messageBroker.adapter;

import org.springframework.stereotype.Component;

import com.unicauca.ms_notifications.domain.ports.IMessageErrorHandlingPort;
import com.unicauca.ms_notifications.infraestructure.output.jpa.entity.MessageProcessingErrorEntity;
import com.unicauca.ms_notifications.infraestructure.output.jpa.repository.IMessageProcessingErrorRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component // Le damos un nombre para evitar ambigüedad si es necesario
@RequiredArgsConstructor
@Slf4j
public class MessageErrorHandlingAdapter implements IMessageErrorHandlingPort {

    private final IMessageProcessingErrorRepository errorRepository;

    @Override
    public void saveProcessingError(String eventType, String errorDescription, String messageData, String entityType) {
        try {
            MessageProcessingErrorEntity errorEntity = new MessageProcessingErrorEntity();
            errorEntity.setEventType(eventType != null ? eventType : "null_event_type");
            errorEntity.setErrorDescription(errorDescription);
            errorEntity.setMessageData(messageData);
            errorEntity.setEntityType(entityType);
            
            errorRepository.save(errorEntity);
            log.info("Processing error saved for entity type: {}, event type: {}", entityType, eventType);
            
        } catch (Exception e) {
            log.error("Failed to save processing error to database: {}", e.getMessage(), e);
        }
    }
    
}
