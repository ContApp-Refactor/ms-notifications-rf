package com.unicauca.ms_notifications.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class ThirdReplica {
    private Long id; // ID interno de la tabla de notificaciones
    private Long thirdPartyId; // El ID que viene del otro microservicio, debe ser único
    private String fullName;
    private String email;
}
