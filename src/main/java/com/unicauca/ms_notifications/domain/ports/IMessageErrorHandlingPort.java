package com.unicauca.ms_notifications.domain.ports;

/**
 * Puerto de salida para el manejo de errores en el procesamiento de mensajes.
 * Proporciona un mecanismo para persistir información de errores cuando
 * las operaciones de procesamiento de mensajes fallan, permitiendo auditoría y depuración.
 */
public interface IMessageErrorHandlingPort {
    /**
     * Guarda la información de un error cuando falla el procesamiento de un mensaje.
     * @param eventType Tipo de evento que falló (puede ser null).
     * @param errorDescription Descripción del error que ocurrió.
     * @param messageData Datos del mensaje en formato JSON.
     * @param entityType Tipo de entidad que se estaba procesando.
     */
    void saveProcessingError(String eventType, String errorDescription, String messageData, String entityType);
}
