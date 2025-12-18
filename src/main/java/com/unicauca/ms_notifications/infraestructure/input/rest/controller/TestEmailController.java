package com.unicauca.ms_notifications.infraestructure.input.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.unicauca.ms_notifications.application.output.IEmailProviderPort;

@RestController
@RequestMapping("/api/notifications")
public class TestEmailController {
     @Autowired
    private IEmailProviderPort emailProvider;

    @Autowired
    private TemplateEngine templateEngine; // Inyectamos el motor para probar la plantilla

    @GetMapping("/send-email")
    public String sendTestEmail(@RequestParam String to) {
        System.out.println("Recibida petición de prueba para enviar correo a: " + to);

        // Simulamos los datos que vendrían de RabbitMQ
        Context context = new Context();
        context.setVariable("clientName", "Cliente de Prueba");
        // No es necesario simular la lista de facturas para esta prueba de conexión
        context.setVariable("invoices", java.util.Collections.emptyList());

        // Procesamos una plantilla simple o la real
        //String htmlBody = "<h1>¡Esto es una prueba!</h1><p>Si recibes esto, la conexión SMTP con Gmail funciona.</p>";
        // O si quieres probar la plantilla real:
        String htmlBody = templateEngine.process("invoice-reminder-template", context);

        try {
            emailProvider.sendInvoiceReminderEmail("Cliente de Prueba", to, htmlBody);
            return "Intento de envío de correo a " + to + " realizado. Revisa la bandeja de entrada y los logs.";
        } catch (Exception e) {
            return "Error al enviar el correo: " + e.getMessage();
        }
    }
}
