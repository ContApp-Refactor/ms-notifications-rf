package com.unicauca.ms_notifications.infraestructure.output.messageBroker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventDtoThird<T, U> {
    private T data;
    private U type;
}