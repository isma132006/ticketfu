package com.ismael.ticketfu.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "tickets")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //ManyToOne es uan relacion muchos a uno, un evento se relaciona con muchos tickets
    //fetchType.Lazy, el fetch = controla cuando se cargan los datos delA db,
    //2 categorias Lazy: perezosa, no carga todo, solo lo necesario
    // EAGER : inmediata, Trae todo de un jalon
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    //Un usuario puede tener muchos tickets,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private TicketStatus ticketStatus;

    @Column(nullable = false, unique = true)
    private String qrCode;

    @Column(nullable = false)
    private LocalDateTime purchasedAt;

    @PrePersist
    public void prePersist() {
        purchasedAt = LocalDateTime.now();
    }


}
