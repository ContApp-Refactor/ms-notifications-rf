package com.unicauca.ms_notifications.infraestructure.output.messageBroker.dto;

import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class InvoiceDueReminderEventDto {
    private Long thirdPartyId; 
    private List<InvoiceDetailEventDto> invoiceDetails;
}
