package com.unicauca.ms_notifications.infraestructure.output.messageBroker.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ThirdUpdatedEventDto {
    private Long thirdId;
    private String entId;
    private String fullName;
    private String email;
    private Boolean state;
}
