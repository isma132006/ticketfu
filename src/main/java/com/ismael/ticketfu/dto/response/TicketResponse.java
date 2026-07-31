package com.ismael.ticketfu.dto.response;

import com.ismael.ticketfu.entity.Ticket;
import com.ismael.ticketfu.entity.TicketStatus;
import com.ismael.ticketfu.repository.EventRepository;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDateTime;

@Data
/**
 * Recibo de compra, la respuesta uwe da el ticket
 * Lo que se respondera al cliente una vez que se haga la compra.
 *
 */
@NoArgsConstructor
public class TicketResponse {
    private Long id;
    private String eventName;
    private String userName;
    private String qrCode;
    private TicketStatus ticketStatus;
    private LocalDateTime purchasedAt;


    public TicketResponse(Ticket ticket) {
        this.id =ticket.getId();
        this.eventName = ticket.getEvent().getName();
        this.userName = ticket.getUser().getEmail();
        this.qrCode = ticket.getQrCode();
        this.ticketStatus = ticket.getTicketStatus();
        this.purchasedAt = ticket.getPurchasedAt();


    }
}
