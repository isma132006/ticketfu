package com.ismael.ticketfu.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashMap;

import java.util.Map;

@Data
public class ErrorResponse {

    private int status;
    private String message;
    private LocalDateTime timestamp;

    // Clave: campo con error (ej. "email"). Valor: descripción (ej. "Formato inválido")
    private Map<String, String> errors;

    public ErrorResponse(int status, String message, Map<String, String> errors) {
        this.status = status;
        this.message = message;
        this.timestamp = LocalDateTime.now();
        this.errors = errors;
    }

    public ErrorResponse(int status, String message) {
        this.status = status;
        this.message = message;
        this.timestamp = LocalDateTime.now();

    }
}