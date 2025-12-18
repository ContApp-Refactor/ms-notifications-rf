package com.unicauca.ms_notifications.application.input;

import com.unicauca.ms_notifications.infraestructure.output.messageBroker.dto.InvoiceDueReminderEventDto;

public interface IProcessNotificationUseCase {
    void processInvoiceDueReminder(InvoiceDueReminderEventDto eventData);
}
