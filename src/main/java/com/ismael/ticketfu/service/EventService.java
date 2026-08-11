package com.ismael.ticketfu.service;

import com.ismael.ticketfu.entity.Category;
import com.ismael.ticketfu.entity.Event;
import com.ismael.ticketfu.exception.ResourceNotFoundException;

import com.ismael.ticketfu.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
@RequiredArgsConstructor
public class EventService {


    private final com.ismael.ticketfu.repository.EventRepository eventRepository;


    private final CategoryRepository categoryRepository;
    @Transactional
    public Event create(Event event) {
        if (eventRepository.existsByName(event.getName())) {
            throw new IllegalArgumentException("El evento ya existe");
        }

        // 👈 Validamos que traiga categoría y la buscamos en la BD para ligarla
        if (event.getCategory() == null || event.getCategory().getId() == null) {
            throw new IllegalArgumentException("El evento debe incluir un objeto category con su id");
        }

        Long categoryId = event.getCategory().getId();
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("No existe la categoría con id: " + categoryId));

        event.setCategory(category); // Inyectamos la entidad real cargada de la BD

        return eventRepository.save(event);
    }




    public List<Event> getAll(){
        return eventRepository.findAll();
    }

    public Event getById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El id está vacío");
        }
        return eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe el evento con id: " + id));
    }
    @Transactional
    public Event update(Long id, Event event) {
        if (id == null || event == null) {
            throw new IllegalArgumentException("Los parámetros no pueden ser nulos");
        }
        Event nuevo = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evento no encontrado con id: " + id));

        nuevo.setName(event.getName());
        nuevo.setDescription(event.getDescription());
        nuevo.setLocalDateTime(event.getLocalDateTime());
        nuevo.setVenue(event.getVenue());
        nuevo.setCapacity(event.getCapacity());
        nuevo.setAvailableTickets(event.getAvailableTickets());
        nuevo.setPrice(event.getPrice());

        if (event.getCategory() != null) {
            nuevo.setCategory(event.getCategory());
        }

        return eventRepository.save(nuevo);
    }
    @Transactional
    public void delete(Long id){
        if (id== null){
            throw  new IllegalArgumentException("EL id esta vacia");
        }
        if(!eventRepository.existsById(id)){
            throw  new ResourceNotFoundException("No existe el evento con ese ID");
        }
        eventRepository.deleteById(id);
    }
    @Transactional
    public List<Event> createAll(List<Event> events) {
        if (events == null || events.isEmpty()) {
            throw new IllegalArgumentException("La lista de eventos no puede estar vacía");
        }

        // saveAll guarda toda la lista de un solo golpe en la BD
        return eventRepository.saveAll(events);
    }
}
