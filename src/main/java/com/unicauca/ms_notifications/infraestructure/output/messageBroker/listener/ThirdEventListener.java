package com.unicauca.ms_notifications.infraestructure.output.messageBroker.listener;

import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import com.rabbitmq.client.Channel;
import com.unicauca.ms_notifications.infraestructure.config.RabbitThirdsEventsConfig;
import com.unicauca.ms_notifications.infraestructure.output.jpa.entity.ThirdReplicaEntity;
import com.unicauca.ms_notifications.infraestructure.output.jpa.repository.IThirdReplicaRepository;
import com.unicauca.ms_notifications.infraestructure.output.messageBroker.dto.EventDtoThird;
import com.unicauca.ms_notifications.infraestructure.output.messageBroker.dto.ThirdUpdatedEventDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ThirdEventListener {
    private final IThirdReplicaRepository thirdReplicaRepository;

    @RabbitListener(queues = RabbitThirdsEventsConfig.THIRD_UPDATED_QUEUE, containerFactory = "rabbitListenerContainerFactory")
    public void handleThirdUpdatedEvent(EventDtoThird<ThirdUpdatedEventDto, String> event,
            Message message, Channel channel,
            @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {

        String eventType = event.getType();
        ThirdUpdatedEventDto thirdUpdatedDTO = event.getData();

        if (thirdUpdatedDTO == null) {
            log.error("Received event with null data. Event type: {}. The message will be discarded.", eventType);
            throw new AmqpRejectAndDontRequeueException("Event data is null");
        }

        log.info("Received event: '{}' for thirdId: {}", eventType, thirdUpdatedDTO.getThirdId());

        try {
            switch (eventType) {
                case "THIRD_UPDATED":
                    // Lógica "Upsert" (Update or Insert)
                    ThirdReplicaEntity replica = thirdReplicaRepository.findByThirdId(thirdUpdatedDTO.getThirdId())
                            .orElse(new ThirdReplicaEntity()); // Si no existe, crea una nueva

                    // Mapea los datos del DTO a la entidad
                    replica.setThirdId(thirdUpdatedDTO.getThirdId());
                    replica.setEnterpriseId(thirdUpdatedDTO.getEntId());
                    replica.setFullName(thirdUpdatedDTO.getFullName());
                    replica.setEmail(thirdUpdatedDTO.getEmail());
                    replica.setActive(thirdUpdatedDTO.getState());

                    thirdReplicaRepository.save(replica);
                    log.info("Third replica for thirdId: {} synchronized successfully.", thirdUpdatedDTO.getThirdId());
                    break;
                default:
                    log.warn("Unhandled event type: {}. The message will be discarded.", eventType);
                    break;
            }

            log.info("Event processing completed for thirdId: {}", thirdUpdatedDTO.getThirdId());
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            log.error("Error al procesar el evento", e);
            throw new AmqpRejectAndDontRequeueException("Error processing event", e);
        }
    }
}
