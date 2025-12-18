package com.unicauca.ms_notifications.infraestructure.output.messageBroker.dto;

import java.time.LocalDate;

import org.springframework.web.context.annotation.RequestScope;

import lombok.Getter;
import lombok.Setter;

@RequestScope
@Getter
@Setter
public class InvoiceDetailEventDto {
    private Long invoiceId;
    private Long invoiceCode;
    private LocalDate expirationDate;
    private Long totalAmount;
    private Long pendingValue;
}
