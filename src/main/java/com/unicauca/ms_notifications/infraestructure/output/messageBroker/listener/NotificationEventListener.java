package com.unicauca.ms_notifications.infraestructure.output.messageBroker.listener;

import com.rabbitmq.client.Channel;
import com.unicauca.ms_notifications.application.input.IProcessNotificationUseCase;
import com.unicauca.ms_notifications.domain.exception.ValidationException;
import com.unicauca.ms_notifications.domain.ports.IMessageErrorHandlingPort;
import com.unicauca.ms_notifications.infraestructure.config.RabbitNotificationsConfig;
import com.unicauca.ms_notifications.infraestructure.output.messageBroker.base.AbstractMessageListener;
import com.unicauca.ms_notifications.infraestructure.output.messageBroker.dto.EventDto;
import com.unicauca.ms_notifications.infraestructure.output.messageBroker.dto.InvoiceDueReminderEventDto;
import com.unicauca.ms_notifications.infraestructure.output.messageBroker.utils.JsonUtils;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import java.util.Objects;

/**
 * @brief Listener to handle notification events from RabbitMQ.
 * It processes events related to invoice due reminders.
 * It extends AbstractMessageListener to leverage common message handling logic.
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventListener extends AbstractMessageListener<EventDto<InvoiceDueReminderEventDto>> {

    private final IProcessNotificationUseCase processNotificationUseCase;

    @Qualifier("messageErrorHandlingAdapter")
    private final IMessageErrorHandlingPort messageErrorHandlingPortImpl;

    @PostConstruct
    private void init() {
        this.messageErrorHandlingPort = messageErrorHandlingPortImpl;
    }

    /**
     * Listens to the notifications queue and processes incoming notification events.
     * @param event The notification event received.
     * @param channel The RabbitMQ channel.
     * @param deliveryTag The delivery tag for message acknowledgment.
     */
    @RabbitListener(queues = RabbitNotificationsConfig.NOTIFICATIONS_QUEUE)
    public void listenToNotificationQueue(
            EventDto<InvoiceDueReminderEventDto> event,
            Channel channel,
            @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        log.info("Evento de Notificación recibido: '{}' para el tercero: {}",
                event.getType(),
                event.getData() != null ? event.getData().getThirdPartyId() : "N/A");
        handleMessage(event, channel, deliveryTag);
    }

    /**
     * @brief Processes the notification event based on its type.
     * @param event The notification event to process.
     * @throws Exception if any error occurs during processing.
     */
    @Override
    protected void processEvent(EventDto<InvoiceDueReminderEventDto> event) {
        String eventType = event.getType();
        InvoiceDueReminderEventDto data = event.getData();
        Long thirdPartyId = data != null ? data.getThirdPartyId() : null;

        try {
            // Log de diagnóstico de TenantContext
            String tenantIdFromContext = com.unicauca.ms_notifications.infraestructure.output.multitenancy.utils.TenantContext
                    .getTenantId();
            log.info("[LISTENER-LOG] Valor de TenantContext ANTES de la operación de BD: '{}'", tenantIdFromContext);

            switch (eventType) {
                case "INVOICE_DUE_REMINDER":
                    processNotificationUseCase.processInvoiceDueReminder(data);
                    break;
                default:
                    log.warn("Evento de notificación recibido con tipo desconocido: {}. Descartando.", eventType);
                    break;
            }
            log.info("Evento de notificación procesado exitosamente. Tipo: {}", eventType);
        } catch (Exception e) {
            log.error("Error de negocio al procesar notificación para el tercero: {}. Error: {}", thirdPartyId,
                    e.getMessage());
            throw e; // Se vuelve a lanzar para que AbstractMessageListener lo capture y lo guarde en BD.
        }
    }

    /**
     * @brief Validates the structure and content of the notification event.
     * @param event The notification event to validate.
     * @throws ValidationException if validation fails.
     */
    @Override
    protected void validateEvent(EventDto<InvoiceDueReminderEventDto> event) throws ValidationException {
        if (event == null) {
            throw new ValidationException("Validation failed: Event is null");
        }
        if (event.getData() == null) {
            throw new ValidationException("Validation failed: Event data is null");
        }
        if (event.getType() == null) {
            throw new ValidationException("Validation failed: Event type is null");
        }

        InvoiceDueReminderEventDto data = event.getData();
        if (data.getThirdPartyId() == null) {
            throw new ValidationException("Validation failed: thirdPartyId is null");
        }
        if (data.getInvoiceDetails() == null || data.getInvoiceDetails().isEmpty()) {
            throw new ValidationException("Validation failed: invoiceDetails is null or empty");
        }
        if (data.getInvoiceDetails().stream().anyMatch(Objects::isNull)) {
            throw new ValidationException("Validation failed: invoiceDetails list contains null items");
        }
        // Si todo está bien, el método simplemente termina. No se lanza ninguna excepción.
    }

    /**
     * @brief Returns the entity type for this listener.
     * @return The entity type as a string.
     */
    @Override
    protected String getEntityType() {
        return "Notification.InvoiceDueReminder";
    }

    /**
     * @brief Extracts the event type from the notification event.
     * @param event The notification event.
     */
    @Override
    protected String extractEventType(EventDto<InvoiceDueReminderEventDto> event) {
        return event != null ? event.getType() : "unknown";
    }

    /**
     * @brief Converts the notification event to a JSON string for logging or error handling.
     * @param event The notification event to convert.
     * @return The JSON representation of the event.
     * @throws Exception if conversion fails.
     */
    @Override
    protected String convertEventToJson(EventDto<InvoiceDueReminderEventDto> event) {
        if (event == null || event.getData() == null) {
            return "{\"error\": \"Event or event data is null\"}";
        }
        return JsonUtils.reminderDtoToJsonWithNullHandling(event.getData());
    }
}