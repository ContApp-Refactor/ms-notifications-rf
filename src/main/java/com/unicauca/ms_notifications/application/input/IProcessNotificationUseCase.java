package com.unicauca.ms_notifications.application.input;

import com.unicauca.ms_notifications.infraestructure.output.messageBroker.dto.InvoiceDueReminderEventDto;

/**
 * @brief Use case interface for processing notifications.
 * Defines the contract for handling invoice due reminder events.
 */
public interface IProcessNotificationUseCase {
    void processInvoiceDueReminder(InvoiceDueReminderEventDto eventData);
}
