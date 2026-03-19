package com.unicauca.ms_notifications.infraestructure.output.jpa.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.unicauca.ms_notifications.domain.model.MessageProcessingError;
import com.unicauca.ms_notifications.infraestructure.output.jpa.entity.MessageProcessingErrorEntity;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IMessageProcessingErrorPersistenceMapper {
    MessageProcessingError toDomain(MessageProcessingErrorEntity entity);
    MessageProcessingErrorEntity toEntity(MessageProcessingError domain);
}
