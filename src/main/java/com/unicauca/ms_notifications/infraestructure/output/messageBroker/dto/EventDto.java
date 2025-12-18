package com.unicauca.ms_notifications.infraestructure.output.messageBroker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EventDto<T> {
    private String type;
    private T data;
}
