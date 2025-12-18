package com.unicauca.ms_notifications.infraestructure.output.email;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.unicauca.ms_notifications.application.output.IEmailProviderPort;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class EmailAdapter implements IEmailProviderPort {

    private final JavaMailSender mailSender;

    @Value("${notification-settings.mail.from}")
    private String fromEmail;

    @Value("${notification-settings.mail.invoice-reminder-subject}")
    private String subject;

    @Override
    public void sendInvoiceReminderEmail(String recipientName, String recipientEmail, String htmlContent) {
        log.info("Intentando enviar correo de recordatorio a: {}", recipientEmail);

        MimeMessage mimeMessage = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(recipientEmail);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // true -> el contenido es HTML

            mailSender.send(mimeMessage);
            log.info("Correo enviado exitosamente a: {}", recipientEmail);

        } catch (MessagingException e) {
            log.error("Error al enviar correo a {}: {}", recipientEmail, e.getMessage());
        }
    }
    
}
