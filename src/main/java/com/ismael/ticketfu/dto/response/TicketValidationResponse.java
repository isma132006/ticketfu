package com.ismael.ticketfu.dto.response;

import com.ismael.ticketfu.entity.TicketStatus;
import lombok.Data;

@Data

public class TicketValidationResponse {
    private boolean valid;
    private String message;

    public TicketValidationResponse(boolean valid, String message) {
        this.valid = valid;
        this.message = message;
    }
}
