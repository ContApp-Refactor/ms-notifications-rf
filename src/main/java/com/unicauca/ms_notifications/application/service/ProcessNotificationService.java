package com.unicauca.ms_notifications.application.service;

import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.unicauca.ms_notifications.application.input.IProcessNotificationUseCase;
import com.unicauca.ms_notifications.application.output.IEmailProviderPort;
import com.unicauca.ms_notifications.application.output.IThirdProviderPort;
import com.unicauca.ms_notifications.domain.model.ThirdReplica;
import com.unicauca.ms_notifications.infraestructure.output.messageBroker.dto.InvoiceDueReminderEventDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProcessNotificationService implements IProcessNotificationUseCase {

    private final IEmailProviderPort emailProviderPort;
    private final TemplateEngine templateEngine;
    private final IThirdProviderPort thirdProviderPort;

    /**
     * @brief Processes an invoice due reminder event.
     * Retrieves third party information, generates email content using a template,
     * and sends the notification email.
     * @param event The invoice due reminder event data.
     * @throws RuntimeException if the third party is not found.
     */
    @Override
    public void processInvoiceDueReminder(InvoiceDueReminderEventDto event) {
        log.info("Procesando recordatorio para el tercero con ID: {}", event.getThirdPartyId());

        ThirdReplica thirdParty = thirdProviderPort.findByThirdPartyId(event.getThirdPartyId())
                .orElseThrow(() -> new RuntimeException("Tercero no encontrado con ID: " + event.getThirdPartyId()));

        Context context = new Context();
        context.setVariable("clientName", thirdParty.getFullName());
        context.setVariable("invoices", event.getInvoiceDetails());

        String htmlBody = templateEngine.process("invoice-reminder-template", context);

        emailProviderPort.sendInvoiceReminderEmail(
                thirdParty.getFullName(),
                thirdParty.getEmail(),
                htmlBody);
    }

}
