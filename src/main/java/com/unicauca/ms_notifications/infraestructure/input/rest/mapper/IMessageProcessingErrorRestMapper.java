package com.unicauca.ms_notifications.infraestructure.input.rest.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.unicauca.ms_notifications.domain.model.MessageProcessingError;
import com.unicauca.ms_notifications.infraestructure.input.rest.dto.response.MessageProcessingErrorResponse;


@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IMessageProcessingErrorRestMapper {

    MessageProcessingErrorResponse toResponse(MessageProcessingError domain);

}