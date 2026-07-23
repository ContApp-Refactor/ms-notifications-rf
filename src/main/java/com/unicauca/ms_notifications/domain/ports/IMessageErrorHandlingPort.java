package com.unicauca.ms_notifications.domain.ports;

/**
 * Output port interface for message error handling in processing of messages.
 * Provides a mechanism to persist error information when
 * message processing operations fail, allowing for auditing and debugging.
 */
public interface IMessageErrorHandlingPort {
    /**
     * Saves error information when message processing fails.
     * @param eventType The type of event that failed (can be null).
     * @param errorDescription Description of the error that occurred.
     * @param messageData Message data in JSON format.
     * @param entityType The type of entity being processed.
     */
    void saveProcessingError(String eventType, String errorDescription, String messageData, String entityType);
}
