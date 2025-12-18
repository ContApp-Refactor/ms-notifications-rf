package com.unicauca.ms_notifications.application.output;

public interface IEmailProviderPort {
    void sendInvoiceReminderEmail(String recipientName, String recipientEmail, String htmlContent);
}
