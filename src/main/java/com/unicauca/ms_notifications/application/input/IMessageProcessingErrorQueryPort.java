package com.unicauca.ms_notifications.application.input;

import com.unicauca.ms_notifications.domain.model.MessageProcessingError;

public interface IMessageProcessingErrorQueryPort {
    MessageProcessingError findLastRecord();
}
