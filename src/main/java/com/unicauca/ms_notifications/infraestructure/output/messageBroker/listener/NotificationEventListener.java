package com.unicauca.ms_notifications.infraestructure.output.messageBroker.listener;

import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import com.rabbitmq.client.Channel;
import com.unicauca.ms_notifications.application.input.IProcessNotificationUseCase;
import com.unicauca.ms_notifications.infraestructure.config.RabbitNotificationsConfig;
import com.unicauca.ms_notifications.infraestructure.output.messageBroker.dto.EventDto;
import com.unicauca.ms_notifications.infraestructure.output.messageBroker.dto.InvoiceDueReminderEventDto;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@AllArgsConstructor
@Slf4j
public class NotificationEventListener {
    private final IProcessNotificationUseCase processNotificationUseCase;

    @RabbitListener(queues = RabbitNotificationsConfig.NOTIFICATIONS_QUEUE)
    public void processWriteOffEvent(EventDto<InvoiceDueReminderEventDto> event,
            Message message, Channel channel,
            @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        String eventType = event.getType();
        InvoiceDueReminderEventDto invoiceDueReminderEventDto = event.getData();
        if (invoiceDueReminderEventDto == null) {
            log.error("Evento de notificación recibido con data nula. Tipo: {}. Descartando.", eventType);
            throw new AmqpRejectAndDontRequeueException("Data del evento de notificación es nula");
        }

        log.info("Evento de Notificación recibido: '{}' para el tercero: {}", eventType, invoiceDueReminderEventDto.getThirdPartyId());
        try {

            // ===== LOG DE DIAGNÓSTICO 6: ¿Qué valor tiene el TenantContext DENTRO del listener? =====
            String tenantIdFromContext = com.unicauca.ms_notifications.infraestructure.output.multitenancy.utils.TenantContext.getTenantId();
            log.info("[LISTENER-LOG] Valor de TenantContext ANTES de la operación de BD: '{}'", tenantIdFromContext);

            switch (eventType) {
                case "INVOICE_DUE_REMINDER":
                    processNotificationUseCase.processInvoiceDueReminder(invoiceDueReminderEventDto);
                    break;
                default:
                    log.warn("Evento de notificación recibido con tipo desconocido: {}. Descartando.", eventType);
                    break;
            }

            log.info("Evento de notificación procesado exitosamente. Tipo: {}", eventType);
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            log.error("Error al procesar el evento de notificación. Tipo: {}. Error: {}", eventType, e.getMessage());
            throw new AmqpRejectAndDontRequeueException("Error al procesar el evento de notificación", e);
        }
    }
}
