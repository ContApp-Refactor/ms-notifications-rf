package com.unicauca.ms_notifications.infraestructure.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import lombok.extern.slf4j.Slf4j;

/**
 * @brief Configuration class for RabbitMQ notifications.
 * Defines exchanges, queues, and bindings for notification messages.
 * This configuration is excluded from the 'test' profile.
 */

@Configuration
@Slf4j
@Profile("!test")
public class RabbitNotificationsConfig {
    public static final String NOTIFICATIONS_EXCHANGE = "notifications.exchange";
    public static final String NOTIFICATIONS_QUEUE = "notifications.queue";

    // STATEMENT EXCHANGES
    @Bean
    FanoutExchange notificationsExchange() {
        return new FanoutExchange(NOTIFICATIONS_EXCHANGE, true, false);
    }

    // STATEMENT OF QUEUES AND BINDINGS
    @Bean
    Queue notificationsQueue() {
        return QueueBuilder.durable(NOTIFICATIONS_QUEUE).build();
    }

    @Bean
    Binding notificationsBinding(FanoutExchange notificationsExchange, Queue notificationsQueue) {
        return BindingBuilder.bind(notificationsQueue).to(notificationsExchange);
    }
}
