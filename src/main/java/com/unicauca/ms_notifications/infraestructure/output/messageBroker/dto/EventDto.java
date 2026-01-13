package com.unicauca.ms_notifications.infraestructure.output.messageBroker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @brief Generic Event Data Transfer Object (DTO) for message broker communication.
 * @param <T> The type of the data payload.
 */

@Data
@AllArgsConstructor
public class EventDto<T> {
    private String type;
    private T data;
}
