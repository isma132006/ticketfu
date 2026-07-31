package com.ismael.ticketfu.repository;

import com.ismael.ticketfu.entity.Event;
import com.ismael.ticketfu.entity.Ticket;
import com.ismael.ticketfu.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.User;

import java.util.List;
import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findByUser(Users user);

    List<Ticket> findByEvent(Event event);

    Optional<Ticket> findByQrCode(String qrCode);

    boolean existsByQrCode(String qrCode);

}