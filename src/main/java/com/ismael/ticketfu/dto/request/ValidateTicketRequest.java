package com.ismael.ticketfu.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
/**
 * Escaneo en la entrada ejemplo, se necesita enviar al servidor el qr,
 * este denbe verificarlo
 */
public class ValidateTicketRequest {
    @NotBlank(message =  "EL QR no debe ser nulo")
    private String qrCode;
}
