package com.unicauca.ms_notifications.application.service;

import org.springframework.stereotype.Service;

import com.unicauca.ms_notifications.application.input.IMessageProcessingErrorCommandPort;
import com.unicauca.ms_notifications.application.input.IMessageProcessingErrorQueryPort;
import com.unicauca.ms_notifications.application.output.IMessageProcessingErrorPersistencePort;
import com.unicauca.ms_notifications.domain.exception.MessageProcessingErrorNotFoundException;
import com.unicauca.ms_notifications.domain.model.MessageProcessingError;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MessageProcessingErrorService implements IMessageProcessingErrorCommandPort, IMessageProcessingErrorQueryPort{
    
    private final IMessageProcessingErrorPersistencePort persistencePort;

    @Override
    public MessageProcessingError findLastRecord() {
        return persistencePort.findLastRecord().orElseThrow(() -> 
            new MessageProcessingErrorNotFoundException("No message processing errors found"));
    }

    @Override
    public void deleteAll() {
        persistencePort.deleteAll();
    }

    @Override
    public MessageProcessingError findById(Long id) {
        return persistencePort.findById(id).orElseThrow(() -> 
            new MessageProcessingErrorNotFoundException("MessageProcessingError with ID " + id + " not found"));
    }
    
}
