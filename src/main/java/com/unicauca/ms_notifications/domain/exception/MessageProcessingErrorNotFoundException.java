package com.unicauca.ms_notifications.domain.exception;

public class MessageProcessingErrorNotFoundException extends RuntimeException {
    public MessageProcessingErrorNotFoundException(String message) {
        super(message);
    }
}