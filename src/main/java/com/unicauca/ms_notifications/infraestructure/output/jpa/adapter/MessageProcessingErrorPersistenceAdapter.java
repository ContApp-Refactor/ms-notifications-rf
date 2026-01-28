package com.unicauca.ms_notifications.infraestructure.output.jpa.adapter;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.unicauca.ms_notifications.application.output.IMessageProcessingErrorPersistencePort;
import com.unicauca.ms_notifications.domain.model.MessageProcessingError;
import com.unicauca.ms_notifications.infraestructure.output.jpa.mapper.IMessageProcessingErrorPersistenceMapper;
import com.unicauca.ms_notifications.infraestructure.output.jpa.repository.IMessageProcessingErrorRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MessageProcessingErrorPersistenceAdapter implements IMessageProcessingErrorPersistencePort {

    private final IMessageProcessingErrorRepository repository;
    private final IMessageProcessingErrorPersistenceMapper mapper;

    @Override
    public Optional<MessageProcessingError> findById(Long id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<MessageProcessingError> findLastRecord() {
        return repository.findFirstByOrderByIdDesc().map(mapper::toDomain);
    }

    @Override
    public void deleteAll() {
        repository.deleteAll();
    }
    
}
