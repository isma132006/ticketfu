package com.ismael.ticketfu.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CreateEventRequest {

    @NotBlank(message = "El nombre del evento es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    private String name;

    @Size(max = 500, message = "La descripción no puede superar los 500 caracteres")
    private String description;

    @NotNull(message = "La categoría es obligatoria")
    private Long categoryId;

    @NotNull(message = "La capacidad es obligatoria")
    @Min(value = 1, message = "La capacidad mínima debe ser de al menos 1 asiento")
    private Integer capacity;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.0", inclusive = true, message = "El precio no puede ser negativo")
    private BigDecimal price;

    @NotBlank(message = "El lugar/recinto (venue) es obligatorio")
    @Size(max = 150, message = "El lugar no puede superar los 150 caracteres")
    private String venue;

    @NotNull(message = "La fecha y hora del evento son obligatorias")
    @Future(message = "La fecha del evento debe ser en el futuro")
    private LocalDateTime eventDate; // Nombre renombrado para mayor claridad
}