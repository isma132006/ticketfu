package com.ismael.ticketfu.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;


@Data
@Entity
@Table(name = "events")
public class Event {


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private  Category category;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "local_date_time", nullable = false)
    private LocalDateTime localDateTime;

    @Column(name = "venue", nullable = false)
    private String venue;

    @Column(name = "capacity", nullable = false)
    private int capacity;

    @Column(name = "available_tickets", nullable = false)
    private int availableTickets;

    @Column(name = "price", nullable = false)
    private BigDecimal price;
}
