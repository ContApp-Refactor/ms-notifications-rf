package com.unicauca.ms_notifications.application.input;

import com.unicauca.ms_notifications.domain.model.MessageProcessingError;

public interface IMessageProcessingErrorQueryPort {

    /**
     * @brief Finds a message processing error by ID
     * @param id Error record identifier
     * @return Optional containing the error record if found
     */
    MessageProcessingError findById(Long id);

    MessageProcessingError findLastRecord();
}
