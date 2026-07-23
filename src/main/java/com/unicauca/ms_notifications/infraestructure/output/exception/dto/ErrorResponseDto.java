package com.unicauca.ms_notifications.infraestructure.output.exception.dto;

import java.time.LocalDateTime;

public record ErrorResponseDto(
    LocalDateTime timestamp,
    int status,
    String error,
    String message,
    String path
) {}
