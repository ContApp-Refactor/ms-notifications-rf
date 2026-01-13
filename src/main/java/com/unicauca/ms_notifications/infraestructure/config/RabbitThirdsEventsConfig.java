package com.unicauca.ms_notifications.infraestructure.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * @brief Configuration class for RabbitMQ thirds events.
 * Defines exchanges, queues, and bindings for third updated events.
 * This configuration is excluded from the 'test' profile.
 */

@Configuration
@Profile("!test")
public class RabbitThirdsEventsConfig {
    public static final String THIRD_UPDATED_EXCHANGE = "third.updated.exchange";
    public static final String THIRD_UPDATED_QUEUE = "third.updated.queue";

    @Bean
    FanoutExchange thirdUpdatedExchange() {
        return new FanoutExchange(THIRD_UPDATED_EXCHANGE, true, false);
    }

    @Bean
    Queue thirdUpdatedQueue() {
        return new Queue(THIRD_UPDATED_QUEUE, true);
    }

    @Bean
    Binding thirdUpdatedQueueBinding() {
        return org.springframework.amqp.core.BindingBuilder.bind(thirdUpdatedQueue()).to(thirdUpdatedExchange());
    }
}
