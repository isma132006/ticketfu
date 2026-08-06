package com.ismael.ticketfu.service;

import com.ismael.ticketfu.dto.request.PurchaseTicketRequest;
import com.ismael.ticketfu.dto.response.TicketResponse;
import com.ismael.ticketfu.dto.response.TicketValidationResponse;
import com.ismael.ticketfu.entity.Event;
import com.ismael.ticketfu.entity.Ticket;
import com.ismael.ticketfu.entity.TicketStatus;
import com.ismael.ticketfu.entity.Users;
import com.ismael.ticketfu.exception.ResourceNotFoundException;
import com.ismael.ticketfu.repository.EventRepository;
import com.ismael.ticketfu.repository.TicketRepository;
import com.ismael.ticketfu.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
public class TicketService {



    private final TicketRepository ticketRepository;


    private final EventRepository eventRepository;

    private final UserRepository userRepository;

    @Transactional
    public Ticket create(Ticket ticket){

        if(ticket == null){
            throw new IllegalArgumentException("El ticket no puede ser nulo");
        }

        if(ticketRepository.existsByQrCode(ticket.getQrCode())){
            throw new IllegalArgumentException("El código QR ya existe");
        }

        return ticketRepository.save(ticket);
    }

    public List<Ticket> getAll(){
        return ticketRepository.findAll();
    }

    public Ticket getById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El id está vacío");
        }
        return ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe el ticket con id: " + id));
    }
    @Transactional
    public Ticket update(Long id, Ticket ticket) {
        if (id == null || ticket == null) {
            throw new IllegalArgumentException("Los parámetros no pueden ser nulos");
        }
        Ticket nuevo = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket no encontrado con id: " + id));


        nuevo.setEvent(ticket.getEvent());
        nuevo.setUser(ticket.getUser());
        nuevo.setTicketStatus(ticket.getTicketStatus());


        return ticketRepository.save(nuevo);
    }
    @Transactional
    public void delete(Long id){
        if (id== null){
            throw  new IllegalArgumentException("El id esta vacia");
        }
        if(!ticketRepository.existsById(id)){
            throw  new ResourceNotFoundException("No existe el ticket con ese ID");
        }
        ticketRepository.deleteById(id);
    }
    @Transactional
    public List<Ticket> createAll(List<Ticket> tickets) {
        if (tickets == null || tickets.isEmpty()) {
            throw new IllegalArgumentException("La lista de ticketos no puede estar vacía");
        }

        // saveAll guarda toda la lista de un solo golpe en la BD
        return ticketRepository.saveAll(tickets);
    }

//============================================================
    //LOGICA AL COMPRAR UN TICKET, ESCANEARLO Y NOTIFICARLO==========
    //=============================================================


    /**
     * Accion de comprar un ticket
     *
     * @param request   el pedido que se manda
     *                  checar conteo de limite de asientos etc
     * @param userEmail
     */
    @Transactional
    public TicketResponse purchaseTicket(PurchaseTicketRequest request, String userEmail) {

        log.info("Iniciando proceso de compra - Evento ID: {}, Usuario Email: {}", request.getEventId(), userEmail);

        Event evento = eventRepository.findByIdWithLock(request.getEventId())
                .orElseThrow(() -> new ResourceNotFoundException("No existe el evento con id: " + request.getEventId()));

        Users user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("No existe el usuario con email: " + userEmail));


        if (evento.getAvailableTickets() <= 0) {
            throw new IllegalStateException("No hay boletos disponibles para este evento.");
        }

        evento.setAvailableTickets(evento.getAvailableTickets() - 1);

        //  Creación y guardado del boleto
        Ticket ticket = new Ticket();
        ticket.setEvent(evento);
        ticket.setUser(user);
        //GEN A QR RANDOM
        ticket.setQrCode(UUID.randomUUID().toString());
        ticket.setTicketStatus(TicketStatus.PURCHASED);

        ticketRepository.save(ticket);
        log.info("Compra completada exitosamente. Ticket ID generado: {} para el usuario: {}", ticket.getId(), user.getId());

        return new TicketResponse(ticket);
    }


    /**
     * Valida un código QR asociado a un boleto.
     *
     * @param id código QR del boleto.
     * @return resultado de la validación del boleto.
     */
    @Transactional
    public TicketValidationResponse validateQr(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("El código QR no puede estar vacío");
        }
        Ticket ticket = ticketRepository.findByQrCode(id)
                .orElseThrow(() -> new ResourceNotFoundException("El código QR no existe"));
        if (ticket.getTicketStatus() == TicketStatus.USED) {
            return new TicketValidationResponse(false, "El boleto ya fue utilizado");
        }
        if (ticket.getTicketStatus() == TicketStatus.CANCELLED) {
            return new TicketValidationResponse(false, "Este boleto fue cancelado");
        }
        ticket.setTicketStatus(TicketStatus.USED);
        ticketRepository.save(ticket);
        return new TicketValidationResponse(true, "Acceso permitido");
    }

    /**
     * Cancela EL ticket, debe checar varias cosas
     * @param id
     */
    @Transactional
    public TicketValidationResponse cancelTicket(Long id){
        if (id == null) {
            throw new IllegalArgumentException("El id del ticket no puede  estar vacío");
        }
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("El Ticket no existe"));
        if (ticket.getTicketStatus() == TicketStatus.USED) {
            return new TicketValidationResponse(false, "No se puede cancelar un ticket que ya fue usado.");
        }
        if (ticket.getTicketStatus() == TicketStatus.CANCELLED) {
            return new TicketValidationResponse(false, "EL ticket ya se encuentra cancelado");
        }
        Event event = ticket.getEvent();
        event.setAvailableTickets(event.getAvailableTickets()+1);
        eventRepository.save(event);
        ticket.setTicketStatus(TicketStatus.CANCELLED);
        ticketRepository.save(ticket);
        return new TicketValidationResponse(true, "Boleto cancelado con exito");
    }

    public TicketResponse getTicket(Long id) {
        Ticket ticket = getById(id); // Usa tu método getById interno
        return new TicketResponse(ticket);
    }



}
