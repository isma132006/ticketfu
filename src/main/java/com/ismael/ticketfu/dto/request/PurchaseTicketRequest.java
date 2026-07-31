package com.ismael.ticketfu.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
/**
 * Representa la solicitud para comprar un boleto.
 * El cliente únicamente envía el id del evento.
 * La identidad del comprador se extrae de forma segura del token JWT.
 */
public class PurchaseTicketRequest {

    @NotNull(message = "El ID del evento es obligatorio")
    private Long eventId;

    // (Opcional) Si permites comprar más de 1 boleto a la vez:
    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "Debes comprar al menos 1 boleto")
    private Integer quantity = 1;
}